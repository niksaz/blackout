package ru.spbau.blackout.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

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
    private String name = UNKNOWN;
    private Game game;
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

            state_updating_loop:
            do {
                GameState currentState = clientGameState.get();
                if (currentState == GameState.WAITING) {
                    final Game currentGame;
                    synchronized (this) {
                        currentGame = game;
                    }
                    if (currentGame != null) {
                        clientGameState.set(game.getGameState());
                        //noinspection SynchronizationOnLocalVariableOrMethodParameter
                        synchronized (currentGame) {
                            currentGame.notify();
                        }
                    }
                } else {
                    clientGameState.set(game.getGameState());
                }

                currentState = clientGameState.get();
                out.writeObject(currentState);
                switch (currentState) {
                    case WAITING:
                        out.writeInt(server.getPlayersNumber());
                        break;
                    case FINISHED:
                        break state_updating_loop;
                    default:
                        break;
                }
                out.flush();

                try {
                    sleep(Network.STATE_UPDATE_CYCLE_MS);
                } catch (InterruptedException ignored) {
                }
            } while (true);
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

    synchronized void setGame(Game game) {
        this.game = game;
    }

    GameState getClientGameState() {
        return clientGameState.get();
    }
}
