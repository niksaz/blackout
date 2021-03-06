package ru.spbau.blackout.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.InflaterInputStream;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.androidfeatures.PlayServices;
import ru.spbau.blackout.database.ChangeablePlayerProfile;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.screens.MenuScreen;
import ru.spbau.blackout.screens.tables.MultiplayerTable;
import ru.spbau.blackout.screens.tables.PlayScreenTable;
import ru.spbau.blackout.serializationutils.EfficientInputStream;
import ru.spbau.blackout.serializationutils.EfficientOutputStream;
import ru.spbau.blackout.serializationutils.EfficientSerializable;
import ru.spbau.blackout.sessionsettings.SessionSettings;
import ru.spbau.blackout.settings.GameSettings;
import ru.spbau.blackout.utils.Uid;
import ru.spbau.blackout.worlds.ClientGameWorld;

import static ru.spbau.blackout.BlackoutGame.DIALOG_PADDING;

/**
 * Task with the purpose of talking to a server: waiting in a queue, getting a game from a server,
 * synchronizing state of a game, passing user inputs to a server (UIServer)
 */
public class AndroidClient implements Runnable, UIServer {

    private static final String TAG = "AndroidClient";
    private static final String WAITING = "Waiting for a game.";
    private static final String READY_TO_START_MS = "Starting a game. Prepare yourself.";

    private final int port;
    private final int players;
    private final MultiplayerTable table;
    private volatile boolean isInterrupted = false;
    private volatile Vector2 velocityToSend = new Vector2();
    private final AtomicReference<AbilityCast> abilityToSend = new AtomicReference<>();
    private volatile GameScreen gameScreen;
    private volatile ObjectInputStream in;
    private volatile ObjectOutputStream out;

    public AndroidClient(MultiplayerTable table, int port, int players) {
        this.table = table;
        this.port = port;
        this.players = players;
    }

    @Override
    public void run() {
        try (DatagramSocket datagramSocket = new DatagramSocket();
             Socket socket = new Socket(Network.SERVER_IP_ADDRESS, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            this.in = in;
            this.out = out;
            socket.setSoTimeout(Network.SOCKET_IO_TIMEOUT_MS);
            datagramSocket.setSoTimeout(Network.SOCKET_IO_TIMEOUT_MS);
            out.writeUTF(BlackoutGame.get().playServicesInCore().getPlayServices().getPlayerName());
            out.writeInt(datagramSocket.getLocalPort());
            out.flush();
            final int serverDatagramPortForVelocity = in.readInt();
            final int serverDatagramPortForAbilities = in.readInt();

            gameStartWaiting(in, out);
            if (isInterrupted) {
                return;
            }
            socket.setSoTimeout(0);

            final DatagramPacket abilityDatagram =
                    new DatagramPacket(new byte[0], 0, socket.getInetAddress(), serverDatagramPortForAbilities);
            new Thread(new AbilityCastSender(datagramSocket, abilityDatagram)).start();

            new Thread(new WinnerGetter(in)).start();

            final ClientGameWorld currentWorld = (ClientGameWorld) gameScreen.gameWorld();
            final byte[] buffer = new byte[Network.DATAGRAM_WORLD_PACKET_SIZE];
            final DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            final DatagramPacket velocityDatagram =
                    new DatagramPacket(new byte[0], 0, socket.getInetAddress(), serverDatagramPortForVelocity);

            while (!isInterrupted) {
                try (ByteArrayOutputStream velocityByteStream = new ByteArrayOutputStream();
                     EfficientOutputStream velocityObjectStream = new EfficientOutputStream(velocityByteStream)
                ) {
                    velocityObjectStream.writeVector2(velocityToSend);
                    final byte[] byteArray = velocityByteStream.toByteArray();
                    velocityDatagram.setData(byteArray);
                    velocityDatagram.setLength(byteArray.length);
                    datagramSocket.send(velocityDatagram);
                } catch (IOException e) {
                    e.printStackTrace();
                    isInterrupted = true;
                }

                datagramSocket.receive(receivedPacket);
                final EfficientInputStream serverWorldStream = new EfficientInputStream(
                        new InflaterInputStream(new ByteArrayInputStream(receivedPacket.getData())));
                currentWorld.setExternalWorldStream(serverWorldStream);
            }
        } catch (UnknownHostException e) {
            Gdx.app.log(TAG, "Don't know about host " + Network.SERVER_IP_ADDRESS);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } finally {
            isInterrupted = true;
            Gdx.app.postRunnable(() -> {
                final MenuScreen menuScreen = table.getMenuScreen();
                menuScreen.changeMiddleTable(PlayScreenTable.getTable(menuScreen));
                if (gameScreen != null) {
                    BlackoutGame.get().screenManager().disposeScreen();
                }
            });
        }
    }

    public synchronized void stop() {
        isInterrupted = true;
        notifyAll();
    }

    @Override
    public void sendSelfVelocity(GameUnit unit, Vector2 velocity) {
        velocityToSend = new Vector2(velocity);
    }

    @Override
    public void sendAbilityCast(Character character, int abilityNum, Vector2 targetOffset) {
        synchronized (abilityToSend) {
            abilityToSend.set(new AbilityCast(abilityNum, targetOffset));
            abilityToSend.notify();
        }
    }

    @Override
    public void waitForOtherPlayers() {
        try {
            out.writeBoolean(!isInterrupted);
            out.flush();

            final boolean gameSuccessfullyStarted = in.readBoolean();
            if (!gameSuccessfullyStarted) {
                isInterrupted = true;
            }
        } catch (IOException ignored) {
            isInterrupted = true;
        } finally {
            // notifying uiServer that loading is done
            synchronized (this) {
                this.notify();
            }
        }
    }

    private void gameStartWaiting(ObjectInputStream in, ObjectOutputStream out)
            throws IOException, ClassNotFoundException {
        GameState gameState;
        do {
            gameState = (GameState) in.readObject();

            switch (gameState) {
                case WAITING:
                    Gdx.app.postRunnable(() ->
                            table.getStatusLabel().setText(WAITING));
                    break;
                case READY_TO_START:
                    Gdx.app.postRunnable(() ->
                            table.getStatusLabel().setText(READY_TO_START_MS));

                    final GameSettings settings = BlackoutGame.get().getPlayerEntity().getGameSettings();

                    SessionSettings sessionSettings = (SessionSettings) in.readObject();
                    sessionSettings.setPlayerUid((Uid) in.readObject());

                    synchronized (this) {
                        Gdx.app.postRunnable(() -> {
                            final ClientGameWorld gameWorld = new ClientGameWorld(sessionSettings, this);
                            gameScreen = new GameScreen(sessionSettings, gameWorld, this, settings);
                            BlackoutGame.get().screenManager().setScreen(gameScreen);
                        });
                        try {
                            wait();
                        } catch (InterruptedException ignored) {
                            isInterrupted = true;
                        }
                    }
                    break;
                default:
                    break;
            }
        } while (gameState == GameState.WAITING && !isInterrupted);
    }

    private class WinnerGetter implements Runnable {

        private final ObjectInputStream objectInputStream;

        WinnerGetter(ObjectInputStream objectInputStream) {
            this.objectInputStream = objectInputStream;
        }

        @Override
        public void run() {
            while (!isInterrupted) {
                try {
                    final String winnerName = (String) objectInputStream.readObject();
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            new Dialog("", BlackoutGame.get().assets().getDefaultSkin()) {
                                {
                                    setMovable(false);
                                    pad(DIALOG_PADDING);
                                    getContentTable().add(winnerName + " has won");
                                    button("Ok").padBottom(DIALOG_PADDING);
                                }

                                @Override
                                protected void result(Object object) {
                                    super.result(object);
                                    this.remove();
                                }
                            }.show(gameScreen.getUi().getStage());
                        }
                    });

                    final PlayServices playServices = BlackoutGame.get().playServicesInCore().getPlayServices();
                    ChangeablePlayerProfile.loadPlayerEntity(null, null);
                    if (winnerName.equals(playServices.getPlayerName())) {
                        switch (players) {
                            case 2:
                                playServices.unlockAchievement(playServices.getDuelistAchievementID());
                                break;

                            case 3:
                                playServices.unlockAchievement(playServices.getBattleOfThreeAchievementID());
                                break;

                            case 4:
                                playServices.unlockAchievement(playServices.getStrategistAchievementID());
                                break;

                            default:
                                break;
                        }
                    }
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                    isInterrupted = true;
                }
            }
        }
    }

    private class AbilityCastSender implements Runnable {

        private final DatagramSocket datagramSocket;
        private final DatagramPacket abilityDatagram;

        public AbilityCastSender(DatagramSocket datagramSocket, DatagramPacket abilityDatagram) {
            this.datagramSocket = datagramSocket;
            this.abilityDatagram = abilityDatagram;
        }

        @Override
        public void run() {
            while (!isInterrupted) {
                if (abilityToSend.get() != null) {
                    try (ByteArrayOutputStream abilityByteStream = new ByteArrayOutputStream();
                         EfficientOutputStream efficientOutputStream = new EfficientOutputStream(abilityByteStream)
                    ) {
                        efficientOutputStream.writeObject(abilityToSend.getAndSet(null));
                        efficientOutputStream.flush();
                        final byte[] arrayToSend = abilityByteStream.toByteArray();
                        abilityDatagram.setData(arrayToSend);
                        abilityDatagram.setLength(arrayToSend.length);
                        datagramSocket.send(abilityDatagram);
                    } catch (IOException e) {
                        e.printStackTrace();
                        isInterrupted = true;
                    }
                } else {
                    waitForAbility();
                }
            }
        }

        private void waitForAbility() {
            synchronized (abilityToSend) {
                if (abilityToSend.get() == null) {
                    try {
                        abilityToSend.wait(Network.SOCKET_IO_TIMEOUT_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        isInterrupted = true;
                    }
                }
            }
        }
    }


    public static class AbilityCast implements EfficientSerializable {

        public int abilityNum;
        public Vector2 target;

        AbilityCast(int abilityNum, Vector2 target) {
            this.abilityNum = abilityNum;
            this.target = target;
        }

        @Override
        public void effectiveWriteObject(EfficientOutputStream out) throws IOException {
            out.writeInt(abilityNum);
            out.writeVector2(target);
        }

        public static AbilityCast effectiveReadObject(EfficientInputStream in) throws IOException {
            final int abilityNum = in.readInt();
            final Vector2 target = in.readVector2();
            return new AbilityCast(abilityNum, target);
        }
    }
}
