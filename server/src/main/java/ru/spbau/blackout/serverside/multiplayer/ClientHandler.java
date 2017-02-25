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

import ru.spbau.blackout.database.PlayerProfile;
import ru.spbau.blackout.network.GameState;
import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.serializationutils.EfficientInputStream;
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
    private volatile PlayerProfile playerProfile;
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
             DatagramSocket datagramSocketVelocity = new DatagramSocket();
             DatagramSocket datagramSocketAbilities = new DatagramSocket();
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            socket.setSoTimeout(Network.SOCKET_IO_TIMEOUT_MS);
            datagramSocketVelocity.setSoTimeout(Network.SOCKET_IO_TIMEOUT_MS);
            name = in.readUTF();
            // name has been set up, game is waiting for name to access database
            synchronized (this) {
                notify();
            }
            final int clientDatagramPort = in.readInt();
            server.log(name + " connected.");

            out.writeInt(datagramSocketVelocity.getLocalPort());
            out.writeInt(datagramSocketAbilities.getLocalPort());
            out.flush();

            gameStartWaiting(in, out);

            socket.setSoTimeout(0);

            new Thread(new VelocityGetter(datagramSocketVelocity)).start();
            new Thread(new AbilityCastGetter(datagramSocketAbilities)).start();
            new Thread(new WinnerSender(out)).start();

            final DatagramPacket worldDatagramPacket =
                    new DatagramPacket(new byte[0], 0, socket.getInetAddress(), clientDatagramPort);

            while (clientGameState != GameState.FINISHED) {
                if (worldInBytes.get() != null) {
                    final byte[] worldToSend = worldInBytes.getAndSet(null);
                    worldDatagramPacket.setData(worldToSend);
                    worldDatagramPacket.setLength(worldToSend.length);
                    datagramSocketVelocity.send(worldDatagramPacket);
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

    void setPlayerProfile(PlayerProfile playerProfile) {
        this.playerProfile = playerProfile;
    }

    PlayerProfile getPlayerProfile() {
        return playerProfile;
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

    private class VelocityGetter implements Runnable {

        private final DatagramSocket datagramSocket;

        VelocityGetter(DatagramSocket datagramSocket) {
            this.datagramSocket = datagramSocket;
        }

        @Override
        public void run() {
            final byte[] buffer = new byte[Network.DATAGRAM_VELOCITY_PACKET_SIZE];
            final DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            while (clientGameState != GameState.FINISHED) {
                try {
                    datagramSocket.receive(receivedPacket);
                    final EfficientInputStream clientsVelocityStream =
                            new EfficientInputStream(new ByteArrayInputStream(receivedPacket.getData()));
                    final Vector2 velocity = clientsVelocityStream.readVector2();
                    velocityFromClient.set(velocity);
                } catch (IOException e) {
                    e.printStackTrace();
                    clientGameState = GameState.FINISHED;
                }
            }
        }
    }

    private class AbilityCastGetter implements Runnable {

        private final DatagramSocket datagramSocket;

        AbilityCastGetter(DatagramSocket datagramSocket) {
            this.datagramSocket = datagramSocket;
        }

        @Override
        public void run() {
            final byte[] buffer = new byte[Network.DATAGRAM_VELOCITY_PACKET_SIZE];
            final DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
            while (clientGameState != GameState.FINISHED) {
                try {
                    datagramSocket.receive(receivedPacket);
                    System.out.println("ABILITY CAST SIZE IS " + receivedPacket.getLength());
                    final EfficientInputStream efficientInputStream =
                            new EfficientInputStream(new ByteArrayInputStream(receivedPacket.getData()));
                    final AbilityCast abilityCast = efficientInputStream.readObject(AbilityCast.class);
                    abilityCastFromClient.set(abilityCast);
                } catch (IOException e) {
                    e.printStackTrace();
                    clientGameState = GameState.FINISHED;
                }
            }
        }
    }

    private class WinnerSender implements Runnable {

        private final ObjectOutputStream objectOutputStream;

        WinnerSender(ObjectOutputStream objectOutputStream) {
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
        synchronized (this) {
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

            try {
                this.wait();
            } catch (InterruptedException e) {
                clientGameState = GameState.FINISHED;
            }
        }
        out.writeBoolean(clientGameState != GameState.FINISHED);
        out.flush();
    }

    public Uid getPlayerUid() {
        return playerUid;
    }
}
