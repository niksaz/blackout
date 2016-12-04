package ru.spbau.blackout.server;

import com.badlogic.gdx.math.Vector2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.gamesession.TestingSessionSettings;
import ru.spbau.blackout.network.GameState;
import ru.spbau.blackout.network.Network;

/**
 * A thread allocated for each client connected to the server. Initially it is waiting to be matched
 * and later acting as the representative of the client in the game.
 */
class ClientThread extends Thread {

    private static final String UNKNOWN = "UNKNOWN";

    private final RoomServer server;
    private final Socket socket;
    private volatile String name = UNKNOWN;
    private volatile TestingSessionSettings session;
    private volatile Hero.Definition hero;
    private volatile Game game;
    private volatile GameState clientGameState = GameState.WAITING;
    private final AtomicReference<byte[]> worldInBytes = new AtomicReference<>();
    private final AtomicReference<Vector2> velocityFromClient = new AtomicReference<>();

    ClientThread(RoomServer server, Socket socket) {
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
            name = in.readUTF();
            final int clientDatagramPort = in.readInt();
            server.log(name + " connected.");

            do {
                final Game game = this.game;
                if (game != null) {
                    clientGameState = game.getGameState();
                }

                final GameState currentState = clientGameState;
                out.writeObject(currentState);
                if (currentState == GameState.READY_TO_START) {
                    out.writeObject(session);
                    out.writeObject(hero);
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

            final Thread clientInputThread = new Thread(() -> {
                do {
                    try {
                        final Vector2 velocity = (Vector2) in.readObject();
                        velocityFromClient.set(velocity);
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                        clientGameState = GameState.FINISHED;
                    }
                } while (clientGameState != GameState.FINISHED);
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

    String getClientName() {
        return name;
    }

    void setGame(Game game, TestingSessionSettings session, Hero.Definition hero) {
        this.session = session;
        this.hero = hero;
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
}
