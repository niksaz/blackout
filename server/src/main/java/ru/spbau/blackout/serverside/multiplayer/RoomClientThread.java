package ru.spbau.blackout.serverside.multiplayer;

import com.badlogic.gdx.math.Vector2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.game_session.TestingSessionSettings;
import ru.spbau.blackout.network.GameState;
import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.serverside.servers.RoomServer;

/**
 * A thread allocated for each client connected to the server. Initially it is waiting to be matched
 * and later acting as the representative of the client in the game.
 */
public class RoomClientThread extends Thread {

    private static final String UNKNOWN = "UNKNOWN";

    private final RoomServer server;
    private final Socket socket;

    private volatile String name = UNKNOWN;
    private volatile TestingSessionSettings session;
    private volatile Character.Definition character;
    private volatile long playerUid;
    private volatile Game game;
    private volatile GameState clientGameState = GameState.WAITING;
    private final AtomicReference<byte[]> worldInBytes = new AtomicReference<>();
    private final AtomicReference<Vector2> velocityFromClient = new AtomicReference<>();

    public RoomClientThread(RoomServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    public void run() {
        try (
            Socket socket = this.socket;
            DatagramSocket datagramSocket = new DatagramSocket();
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            socket.setSoTimeout(Network.SOCKET_IO_TIMEOUT_MS);
            datagramSocket.setSoTimeout(Network.SOCKET_IO_TIMEOUT_MS);
            name = in.readUTF();
            final int clientDatagramPort = in.readInt();
            server.log(name + " connected.");

            out.writeInt(datagramSocket.getLocalPort());
            out.flush();

            gameStartWaiting(in, out);

            final Thread clientInputThread = new Thread(() -> {
                final byte[] buffer = new byte[Network.DATAGRAM_VELOCITY_PACKET_SIZE];
                final DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
                try {
                    while (clientGameState != GameState.FINISHED) {
                        datagramSocket.receive(receivedPacket);
                        final ObjectInputStream clientsVelocityStream =
                                new ObjectInputStream(new ByteArrayInputStream(receivedPacket.getData()));
                        final Vector2 velocity = (Vector2) clientsVelocityStream.readObject();
                        velocityFromClient.set(velocity);
                    }
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                    clientGameState = GameState.FINISHED;
                }
            });
            clientInputThread.start();

            final DatagramPacket worldDatagramPacket =
                    new DatagramPacket(new byte[0], 0, socket.getInetAddress(), clientDatagramPort);

            while (clientGameState != GameState.FINISHED) {
                if (worldInBytes.get() != null) {
                    final byte[] worldToSend = worldInBytes.getAndSet(null);
                    worldDatagramPacket.setData(worldToSend);
                    worldDatagramPacket.setLength(worldToSend.length);
                    datagramSocket.send(worldDatagramPacket);
                } else {
                    synchronized (this) {
                        if (worldInBytes.get() == null) {
                            try {
                                wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (clientGameState == GameState.WAITING) {
                server.discard(this);
            }
            clientGameState = GameState.FINISHED;
        }
    }

    public String getClientName() {
        return name;
    }

    void setGame(Game game, TestingSessionSettings session, Character.Definition character, long playerUid) {
        this.session = session;
        this.character = character;
        this.playerUid = playerUid;
        this.game = game;
    }

    synchronized void setWorldToSend(byte[] worldInBytes) {
        this.worldInBytes.set(worldInBytes);
        notify();
    }

    GameState getClientGameState() {
        return clientGameState;
    }

    AtomicReference<Vector2> getVelocityFromClient() {
        return velocityFromClient;
    }

    private void gameStartWaiting(ObjectInputStream in, ObjectOutputStream out) throws IOException {
        do {
            final Game game = this.game;
            if (game != null) {
                clientGameState = game.getGameState();
            }

            final GameState currentState = clientGameState;
            out.writeObject(currentState);
            if (currentState == GameState.READY_TO_START) {
                out.writeObject(session);
                out.writeObject(character);
                out.writeLong(playerUid);
                out.writeObject(worldInBytes.getAndSet(null));
                out.flush();

                // loading may take a long time
                socket.setSoTimeout(0);
                // get boolean from the client when he will load the game components
                boolean success = in.readBoolean();
                if (!success) {
                    clientGameState = GameState.FINISHED;
                }
                assert game != null;
                //noinspection SynchronizationOnLocalVariableOrMethodParameter
                synchronized (game) {
                    game.notify();
                }
                socket.setSoTimeout(Network.SOCKET_IO_TIMEOUT_MS);
            } else {
                out.flush();
            }

            if (clientGameState == GameState.WAITING) {
                try {
                    sleep(Network.STATE_UPDATE_CYCLE_MS);
                } catch (InterruptedException ignored) {
                }
            }
        } while (clientGameState == GameState.WAITING);
    }
}
