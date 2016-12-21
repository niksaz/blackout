package ru.spbau.blackout.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicReference;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.sessionsettings.SessionSettings;
import ru.spbau.blackout.ingameui.settings.AbilityIconSettings;
import ru.spbau.blackout.ingameui.settings.IngameUISettings;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.screens.MenuScreen;
import ru.spbau.blackout.screens.MultiplayerTable;
import ru.spbau.blackout.screens.PlayScreenTable;
import ru.spbau.blackout.settings.GameSettings;
import ru.spbau.blackout.worlds.ClientGameWorld;

/**
 * Task with the purpose of talking to a server: waiting in a queue, getting a game from a server,
 * synchronizing state of a game, passing user inputs to a server (UIServer)
 */
public class AndroidClient implements Runnable, UIServer {

    private static final String TAG = "AndroidClient";
    private static final String WAITING = "Waiting for a game.";
    private static final String READY_TO_START_MS = "Starting a game. Prepare yourself.";

    private final MultiplayerTable table;
    private volatile boolean isInterrupted = false;
    private final AtomicReference<Vector2> velocityToSend = new AtomicReference<>();
    private final AtomicReference<AbilityCast> abilityToSend = new AtomicReference<>();
    private GameScreen gameScreen;

    public AndroidClient(MultiplayerTable table) {
        this.table = table;
    }

    @Override
    public void run() {
        try (
            DatagramSocket datagramSocket = new DatagramSocket();
            Socket socket = new Socket(Network.SERVER_IP_ADDRESS, Network.SERVER_TCP_PORT_NUMBER);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            socket.setSoTimeout(Network.SOCKET_IO_TIMEOUT_MS);
            datagramSocket.setSoTimeout(Network.SOCKET_IO_TIMEOUT_MS);
            out.writeUTF(BlackoutGame.get().playServicesInCore().getPlayServices().getPlayerName());
            out.writeInt(datagramSocket.getLocalPort());
            out.flush();

            final int serverDatagramPort = in.readInt();

            gameStartWaiting(in, out);
            if (isInterrupted) {
                return;
            }

            new Thread(new UIChangeSenderUDP(socket.getInetAddress(), serverDatagramPort, datagramSocket)).start();
            new Thread(new UIChangeSenderTCP(out)).start();

            final ClientGameWorld currentWorld = (ClientGameWorld) gameScreen.gameWorld();
            final byte[] buffer = new byte[Network.DATAGRAM_WORLD_PACKET_SIZE];
            final DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

            while (!isInterrupted) {
                datagramSocket.receive(receivedPacket);
                final ObjectInputStream serverWorldStream =
                        new ObjectInputStream(new ByteArrayInputStream(receivedPacket.getData()));
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
                BlackoutGame.get().screenManager().setScreen(menuScreen);
            });
        }
    }

    public synchronized void stop() {
        isInterrupted = true;
        notifyAll();
    }

    @Override
    public void sendSelfVelocity(GameUnit unit, Vector2 velocity) {
        synchronized (velocityToSend) {
            velocityToSend.set(new Vector2(velocity));
            velocityToSend.notify();
        }
    }

    @Override
    public void sendAbilityCast(GameUnit unit, int abilityNum, Vector2 target) {
        synchronized (abilityToSend) {
            abilityToSend.set(new AbilityCast(abilityNum, target));
            abilityToSend.notify();
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

                    AbilityIconSettings firstIconSettings = new AbilityIconSettings(0);
                    IngameUISettings uiSettings = new IngameUISettings(new AbilityIconSettings[] { firstIconSettings });
                    GameSettings settings = new GameSettings(uiSettings);  // just default settings

                    SessionSettings sessionSettings = (SessionSettings) in.readObject();
                    sessionSettings.playerUid = in.readLong();

                    // using the fact that AndroidClient is UIServer itself.
                    // so synchronizing on server on loading
                    synchronized (this) {
                        Gdx.app.postRunnable(() -> {
                            final ClientGameWorld gameWorld = new ClientGameWorld(sessionSettings.getDefinitions());
                            gameScreen = new GameScreen(sessionSettings, gameWorld, this, settings);
                            BlackoutGame.get().screenManager().setScreen(gameScreen);
                        });
                        try {
                            wait();
                        } catch (InterruptedException ignored) {
                            isInterrupted = true;
                        }
                    }
                    out.writeBoolean(!isInterrupted);
                    out.flush();

                    break;
                default:
                    break;
            }
        } while (gameState == GameState.WAITING && !isInterrupted);
    }

    private class UIChangeSenderUDP implements Runnable {

        private final InetAddress serverInetAddress;
        private final int serverDatagramPort;
        private final DatagramSocket datagramSocket;

        public UIChangeSenderUDP(InetAddress serverInetAddress, int serverDatagramPort, DatagramSocket datagramSocket) {
            this.serverInetAddress = serverInetAddress;
            this.serverDatagramPort = serverDatagramPort;
            this.datagramSocket = datagramSocket;
        }

        @Override
        public void run() {
            final DatagramPacket velocityDatagram =
                    new DatagramPacket(new byte[0], 0, serverInetAddress, serverDatagramPort);

            while (!isInterrupted) {
                if (velocityToSend.get() != null) {
                    try (
                            ByteArrayOutputStream velocityByteStream =
                                    new ByteArrayOutputStream(Network.DATAGRAM_VELOCITY_PACKET_SIZE);
                            ObjectOutputStream velocityObjectStream = new ObjectOutputStream(velocityByteStream)
                    ) {
                        velocityObjectStream.writeObject(velocityToSend.getAndSet(null));
                        velocityObjectStream.flush();
                        final byte[] byteArray = velocityByteStream.toByteArray();
                        velocityDatagram.setData(byteArray);
                        velocityDatagram.setLength(byteArray.length);
                        datagramSocket.send(velocityDatagram);
                    } catch (IOException e) {
                        e.printStackTrace();
                        isInterrupted = true;
                    }
                } else {
                    synchronized (velocityToSend) {
                        if (velocityToSend.get() == null) {
                            try {
                                // setting timeout because if client not responding in SOCKET_IO_TIMEOUT_MS
                                // he will be disconnected
                                velocityToSend.wait(Network.SOCKET_IO_TIMEOUT_MS / 2);
                                if (velocityToSend.get() == null) {
                                    velocityToSend.set(new Vector2());
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    private class UIChangeSenderTCP implements Runnable {

        private final ObjectOutputStream objectOutputStream;

        public UIChangeSenderTCP(ObjectOutputStream objectOutputStream) {
            this.objectOutputStream = objectOutputStream;
        }

        @Override
        public void run() {
            while (!isInterrupted) {
                if (abilityToSend.get() != null) {
                    try {
                        objectOutputStream.writeObject(abilityToSend.getAndSet(null));
                        objectOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        isInterrupted = true;
                    }
                } else {
                    synchronized (abilityToSend) {
                        if (abilityToSend.get() == null) {
                            try {
                                abilityToSend.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public static class AbilityCast {
        public int abilityNum;
        public Vector2 target;

        AbilityCast(int abilityNum, Vector2 target) {
            this.abilityNum = abilityNum;
            this.target = target;
        }
    }
}
