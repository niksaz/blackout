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
import java.net.SocketException;
import java.net.UnknownHostException;

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

import static java.lang.Thread.sleep;

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
    private volatile Vector2 velocityToSend = new Vector2();
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
            System.out.println("Hello");
            socket.setSoTimeout(Network.SOCKET_IO_TIMEOUT_MS);
            System.out.println("Hello");
            out.writeUTF(BlackoutGame.getInstance().getPlayServicesInCore().getPlayServices().getPlayerName());
            System.out.println("Hello");
            out.writeInt(datagramSocket.getLocalPort());
            System.out.println("Hello");
            out.flush();
            System.out.println("Hello");

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

            final Thread outputToServerThread = new Thread(() -> {
                long timeLastIterationFinished = System.currentTimeMillis();
                while (!isInterrupted) {
                    try {
                        Vector2 sending = new Vector2(velocityToSend);
                        out.writeObject(sending);
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        isInterrupted = true;
                    }

                    //making sure that we are sending position with desired rate
                    final long duration = timeLastIterationFinished - System.currentTimeMillis();
                    if (duration < Network.TIME_SHOULD_BE_SPENT_FOR_ITERATION) {
                        try {
                            sleep(Network.TIME_SHOULD_BE_SPENT_FOR_ITERATION - duration);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            isInterrupted = true;
                        }
                    }
                    timeLastIterationFinished = System.currentTimeMillis();
                }
            });
            outputToServerThread.start();

            final GameWorldWithExternalSerial currentWorld = (GameWorldWithExternalSerial) gameScreen.getGameWorld();
            // waiting for the first world to arrive
            //socket.setSoTimeout(0);
            while (!isInterrupted) {
                // should read game world here from inputStream

                byte[] buffer = new byte[Network.DATAGRAM_PACKET_SIZE];
                final DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(receivedPacket);
                final ObjectInputStream serverWorldStream =
                        new ObjectInputStream(new ByteArrayInputStream(receivedPacket.getData()));
                currentWorld.setExternalWorldStream(serverWorldStream);

                // should get worlds regularly after the first one
                //socket.setSoTimeout(Network.SOCKET_IO_TIMEOUT_MS);
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
        velocityToSend = velocity;
    }
}
