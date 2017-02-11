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

import ru.spbau.blackout.network.GameState;
import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.serverside.servers.RoomServer;
import ru.spbau.blackout.sessionsettings.SessionSettings;
import ru.spbau.blackout.utils.Uid;

import static java.lang.Thread.sleep;
import static ru.spbau.blackout.network.AndroidClient.AbilityCast;

/**
 * A thread allocated for each client connected to the server. Initially it is waiting to be matched
 * and later acting as the representative of the client in the game.
 */
public class ClientHandler implements Runnable {

    public static final String UNKNOWN = "UNKNOWN";

    private final RoomServer server;
    private final Socket socket;

    private volatile String name = UNKNOWN;
    private volatile SessionSettings session;
    private volatile Uid playerUid;
    private volatile Game game;
    private volatile GameState clientGameState = GameState.WAITING;
    private final AtomicReference<byte[]> worldInBytes = new AtomicReference<>();
    private final AtomicReference<Vector2> velocityFromClient = new AtomicReference<>();
    private final AtomicReference<AbilityCast> abilityCastFromClient = new AtomicReference<>();
    private final AtomicReference<String> winnerName = new AtomicReference<>();

    public ClientHandler(RoomServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    public void run() {
        try (Socket socket = this.socket;
             DatagramSocket datagramSocket = new DatagramSocket();
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            socket.setSoTimeout(Network.SOCKET_IO_TIMEOUT_MS);
            datagramSocket.setSoTimeout(Network.SOCKET_IO_TIMEOUT_MS);
            name = in.readUTF();
            // name has been set up, game is waiting for name to access database
            synchronized (this) {
                notify();
            }
            final int clientDatagramPort = in.readInt();
            server.log(name + " connected.");

            out.writeInt(datagramSocket.getLocalPort());
            out.flush();

            gameStartWaiting(in, out);

            socket.setSoTimeout(0);

            new Thread(new UIChangeGetterUDP(datagramSocket)).start();
            new Thread(new UIChangeGetterTCP(in)).start();
            new Thread(new WinnerSenderTCP(out)).start();

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

    void setGame(Game game, SessionSettings session, Uid playerUid) {
        this.session = session;
        this.playerUid = playerUid;
        this.game = game;
    }

    synchronized void setWorldToSend(byte[] worldInBytes) {
        this.worldInBytes.set(worldInBytes);
        notify();
    }

    public void setWinnerName(String winnerName) {
        synchronized (this.winnerName) {
            this.winnerName.set(winnerName);
            this.winnerName.notify();
        }
    }

    GameState getClientGameState() {
        return clientGameState;
    }

    Vector2 getVelocityFromClient() {
        return velocityFromClient.getAndSet(null);
    }

    AbilityCast getAbilityCastFromClient() {
        return abilityCastFromClient.getAndSet(null);
    }

    private class UIChangeGetterUDP implements Runnable {

        private final DatagramSocket datagramSocket;

        UIChangeGetterUDP(DatagramSocket datagramSocket) {
            this.datagramSocket = datagramSocket;
        }

        @Override
        public void run() {
            final byte[] buffer = new byte[Network.DATAGRAM_VELOCITY_PACKET_SIZE];
            final DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            while (clientGameState != GameState.FINISHED) {
                try {
                    datagramSocket.receive(receivedPacket);
                    final ObjectInputStream clientsVelocityStream =
                            new ObjectInputStream(new ByteArrayInputStream(receivedPacket.getData()));
                    final Vector2 velocity = (Vector2) clientsVelocityStream.readObject();
                    velocityFromClient.set(velocity);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                    clientGameState = GameState.FINISHED;
                }
            }
        }
    }

    private class UIChangeGetterTCP implements Runnable {

        private final ObjectInputStream objectInputStream;

        UIChangeGetterTCP(ObjectInputStream objectInputStream) {
            this.objectInputStream = objectInputStream;
        }

        @Override
        public void run() {
            while (clientGameState != GameState.FINISHED) {
                try {
                    final AbilityCast abilityCast = (AbilityCast) objectInputStream.readObject();
                    abilityCastFromClient.set(abilityCast);
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                    clientGameState = GameState.FINISHED;
                }
            }
        }
    }

    private class WinnerSenderTCP implements Runnable {

        private final ObjectOutputStream objectOutputStream;

        WinnerSenderTCP(ObjectOutputStream objectOutputStream) {
            this.objectOutputStream = objectOutputStream;
        }

        @Override
        public void run() {
            while (clientGameState != GameState.FINISHED) {
                if (winnerName.get() != null) {
                    try {
                        objectOutputStream.writeObject(winnerName.getAndSet(null));
                        objectOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        clientGameState = GameState.FINISHED;
                    }
                } else {
                    synchronized (winnerName) {
                        if (winnerName.get() == null) {
                            try {
                                winnerName.wait(Network.SOCKET_IO_TIMEOUT_MS);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                clientGameState = GameState.FINISHED;
                            }
                        }
                    }
                }
            }
        }
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
                out.writeObject(playerUid);
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

    public Uid getPlayerUid() {
        return playerUid;
    }
}
