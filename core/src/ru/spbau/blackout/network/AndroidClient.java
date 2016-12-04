package ru.spbau.blackout.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicReference;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.gamesession.TestingSessionSettings;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.screens.MenuScreen;
import ru.spbau.blackout.screens.MultiplayerTable;
import ru.spbau.blackout.screens.PlayScreenTable;
import ru.spbau.blackout.settings.GameSettings;
import ru.spbau.blackout.worlds.GameWorldWithExternalSerial;

/**
 * Task with the purpose of talking to a server: waiting in a queue, getting a game from a server,
 * synchronizing state of a game, passing user inputs to a server (AbstractServer)
 */
public class AndroidClient implements Runnable, AbstractServer {

    private static final String TAG = "AndroidClient";
    private static final String WAITING = "Waiting for a game.";
    private static final String READY_TO_START_MS = "Starting a game. Prepare yourself.";

    private final MultiplayerTable table;
    private volatile boolean isInterrupted = false;
    private final AtomicReference<Vector2> velocityToSend = new AtomicReference<>();
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
            out.writeUTF(BlackoutGame.getInstance().getPlayServicesInCore().getPlayServices().getPlayerName());
            out.writeInt(datagramSocket.getLocalPort());
            out.flush();

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

                        final GameSettings settings = new GameSettings();
                        TestingSessionSettings room = (TestingSessionSettings) in.readObject();
                        room.character = (Hero.Definition) in.readObject();

                        // using the fact that AndroidClient is AbstractServer itself.
                        // so synchronizing on server on loading
                        synchronized (this) {
                            gameScreen = new GameScreen(room, new GameWorldWithExternalSerial(), this, settings);
                            BlackoutGame.getInstance().getScreenManager().setScreen(gameScreen);
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

            if (isInterrupted) {
                return;
            }

            final Thread outputToServerThread = new Thread(() -> {
                try {
                    while (!isInterrupted) {
                        if (velocityToSend.get() != null) {
                            final Vector2 sending = velocityToSend.getAndSet(null);
                            out.writeObject(sending);
                            out.flush();
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
                } catch (IOException e) {
                    e.printStackTrace();
                    isInterrupted = true;
                }
            });
            outputToServerThread.start();

            final GameWorldWithExternalSerial currentWorld = (GameWorldWithExternalSerial) gameScreen.getGameWorld();
            final byte[] buffer = new byte[Network.DATAGRAM_WORLD_PACKET_SIZE];
            final DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

            while (!isInterrupted) {
                datagramSocket.receive(receivedPacket);
                final ObjectInputStream serverWorldStream =
                        new ObjectInputStream(new ByteArrayInputStream(receivedPacket.getData()));
                currentWorld.setExternalWorldStream(serverWorldStream);

                // should get worlds regularly after the first one
                datagramSocket.setSoTimeout(Network.SOCKET_IO_TIMEOUT_MS);
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
                BlackoutGame.getInstance().getScreenManager().setScreen(menuScreen);
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
}
