package ru.spbau.blackout.server;

import com.badlogic.gdx.math.Vector2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

import ru.spbau.blackout.GameWorld;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.network.GameState;
import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.gamesession.TestingSessionSettings;

import static ru.spbau.blackout.network.Network.FRAMES_60_SLEEP_MS;

/**
 * A thread allocated for each client connected to the server. Initially it is waiting to be matched
 * and later acting as the representative of the client in the game.
 */
class ClientThread extends Thread {

    private static final String UNKNOWN = "UNKNOWN";

    private final RoomServer server;
    private final Socket socket;
    private volatile String name = UNKNOWN;
    private volatile int numberInGame;
    private volatile TestingSessionSettings session;
    private volatile Hero.Definition hero;
    private AtomicReference<Game> game = new AtomicReference<>();
    private AtomicReference<GameState> clientGameState = new AtomicReference<>(GameState.WAITING);

    ClientThread(RoomServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    public void run() {
        try (
            Socket socket = this.socket;
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            socket.setSoTimeout(Network.SOCKET_IO_TIMEOUT_MS);
            name = in.readUTF();
            server.log(name + " connected.");

            do {
                final Game game = this.game.get();
                if (game != null) {
                    clientGameState.set(game.getGameState());
                }

                final GameState currentState = clientGameState.get();
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
                        clientGameState.set(GameState.FINISHED);
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

                if (clientGameState.get() == GameState.WAITING) {
                    try {
                        sleep(Network.STATE_UPDATE_CYCLE_MS);
                    } catch (InterruptedException ignored) {
                    }
                }
            } while (clientGameState.get() == GameState.WAITING);

            final Thread clientInputThread = new Thread(() -> {
                do {
                    try {
                        final Vector2 velocity = (Vector2) in.readObject();
                        if (velocity != null) {
                            game.get().setVelocityFor(numberInGame, velocity);
                        }
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                        break;
                    }
                } while (clientGameState.get() != GameState.FINISHED);
            });
            clientInputThread.start();

            while (clientGameState.get() != GameState.FINISHED) {
                final GameWorld gameWorld = game.get().getGameWorld();
                //noinspection SynchronizationOnLocalVariableOrMethodParameter
                synchronized (gameWorld) {
                    try {
                        gameWorld.inplaceSerialize(out);
                        out.flush();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException ignored) {
                    }
                }

                // sleep and after getting GameWorld periodically sending it to the client
                try {
                    sleep(FRAMES_60_SLEEP_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ignored) {
        } finally {
            if (clientGameState.get() == GameState.WAITING) {
                server.discard(this);
            }
            clientGameState.set(GameState.FINISHED);
        }
    }

    String getClientName() {
        return name;
    }

    void setGame(Game game, int numberInGame, TestingSessionSettings session, Hero.Definition hero) {
        this.numberInGame = numberInGame;
        this.session = session;
        this.hero = hero;
        this.game.set(game);
    }

    GameState getClientGameState() {
        return clientGameState.get();
    }
}