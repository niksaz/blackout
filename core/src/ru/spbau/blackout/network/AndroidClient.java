package ru.spbau.blackout.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicReference;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.game_session.TestingSessionSettings;
import ru.spbau.blackout.ingameui.settings.AbilityIconSettings;
import ru.spbau.blackout.ingameui.settings.IngameUISettings;
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
        try {
            final String url = "http://" +
                    Network.SERVER_IP_ADDRESS +
                    ':' +
                    Network.SERVER_HTTP_PORT_NUMBER +
                    "/login";


            final URL urlObject = new URL(url);
            final HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            try (
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())
            ) {
                outputStream.writeUTF(BlackoutGame.get().playServicesInCore().getPlayServices().getPlayerName());
            }

            final int responseCode = connection.getResponseCode();
            System.out.println("Sending " + connection.getRequestMethod() + " request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))
            ) {
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    System.out.println(inputLine);
                }
            }
        } catch (Exception e) {
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

    //@Override
    public void rrun() {
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

                        TestingSessionSettings room = (TestingSessionSettings) in.readObject();
                        room.character = (Character.Definition) in.readObject();

                        // using the fact that AndroidClient is AbstractServer itself.
                        // so synchronizing on server on loading
                        synchronized (this) {
                            gameScreen = new GameScreen(room, new GameWorldWithExternalSerial(), this, settings);
                            BlackoutGame.get().screenManager().setScreen(gameScreen);
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
                final DatagramPacket velocityDatagram =
                        new DatagramPacket(new byte[0], 0, socket.getInetAddress(), serverDatagramPort);

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
            });
            outputToServerThread.start();

            final GameWorldWithExternalSerial currentWorld = (GameWorldWithExternalSerial) gameScreen.gameWorld();
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
}
