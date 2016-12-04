package ru.spbau.blackout.server;

import com.badlogic.gdx.math.Vector2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

import ru.spbau.blackout.GameWorld;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.network.GameState;
import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.game_session.TestingSessionSettings;

import static ru.spbau.blackout.network.Network.FRAMES_60_SLEEP_MS;

/**
 * A thread allocated for each client connected to the server. Initially it is waiting to be matched
 * and later acting as the representative of the client in the game.
 */
class ClientThread extends Thread {

    private static final String UNKNOWN = "UNKNOWN";

    private final RoomServer server;
    private final Socket socket;
    private String name = UNKNOWN;
    private volatile int numberInGame;
    private AtomicReference<Game> game = new AtomicReference<>();
    private AtomicReference<GameState> clientGameState = new AtomicReference<>(GameState.WAITING);
    private TestingSessionSettings sessionSettings;
    private Character.Definition clientCharacter;

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

            synchronized (this) {
                do {
                    final Game currentGame = game.get();
                    if (currentGame != null) {
                        clientGameState.set(currentGame.getGameState());
                        //noinspection SynchronizationOnLocalVariableOrMethodParameter
                        synchronized (currentGame) {
                            currentGame.notify();
                        }
                    }

                    final GameState currentState = clientGameState.get();
                    out.writeObject(currentState);
                    if (currentState == GameState.FINISHED) {
                        break;
                    }
                    out.flush();

                    try {
                        sleep(Network.STATE_UPDATE_CYCLE_MS);
                    } catch (InterruptedException ignored) {
                    }
                } while (clientGameState.get() == GameState.WAITING);

                if (clientGameState.get() == GameState.FINISHED) {
                    return;
                }

                // waiting for game to set room to send
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                out.writeObject(sessionSettings);
                out.writeObject(clientCharacter);
                out.flush();

                new Thread(() -> {
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
                }).start();

                while (true) {
                    // sleep and after getting GameWorld periodically sending it to the client
                    try {
                        sleep(FRAMES_60_SLEEP_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final GameWorld gameWorld = game.get().getGameWorld();
                    //noinspection SynchronizationOnLocalVariableOrMethodParameter
                    synchronized (gameWorld) {
                        try {
                            //InplaceSerializable.inplaceSerialize(gameWorld, out);
                            gameWorld.inplaceSerialize(out);
                            out.flush();
                            server.log("Sent to " + numberInGame);
                            // FIXME
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException ignored) {
                        }
                    }
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

    void setGame(Game game, int numberInGame) {
        this.game.set(game);
        this.numberInGame = numberInGame;
    }

    synchronized void setSessionSettings(TestingSessionSettings testingSessionSettings, Character.Definition character) {
        sessionSettings = testingSessionSettings;
        clientCharacter = character;
        notify();
    }

    GameState getClientGameState() {
        return clientGameState.get();
    }
}
