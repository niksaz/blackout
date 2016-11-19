package ru.spbau.blackout.server;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import ru.spbau.blackout.network.GameState;
import ru.spbau.blackout.network.Network;

class Game extends Thread {

    private static AtomicInteger gamesCreated = new AtomicInteger();

    private final int gameId;
    private final RoomServer server;
    private final List<ClientThread> clients;
    private AtomicReference<GameState> gameState = new AtomicReference<>(GameState.READY_TO_START);

    Game(RoomServer server, List<ClientThread> clients) {
        gameId = gamesCreated.getAndAdd(1);
        this.server = server;
        this.clients = clients;
    }

    public void run() {
        server.log("New game with id #" + gameId + " has just started!");
        for (ClientThread thread : clients) {
            thread.setGame(this);
        }
        for (ClientThread thread : clients) {
            GameState currentClientGameState = thread.getClientGameState();
            while (currentClientGameState == GameState.WAITING) {
                try {
                    sleep(Network.WAITING_TO_READY_CYCLE_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentClientGameState = thread.getClientGameState();
            }
            if (currentClientGameState == GameState.FINISHED) {
                gameState.set(GameState.FINISHED);
            }
        }

        if (gameState.get() == GameState.FINISHED) {
            return;
        }

        // put code to create game here
        gameState.set(GameState.IN_PROCESS);

        do {
            for (ClientThread thread : clients) {
                GameState currentClientGameState = thread.getClientGameState();
                if (currentClientGameState == GameState.FINISHED) {
                    server.log(thread.getClientName() +
                               " disconnected. Game with id #" + gameId + " will be finished.");
                    gameState.set(GameState.FINISHED);
                    break;
                }
            }
        } while (gameState.get() != GameState.FINISHED);
    }

    GameState getGameState() {
        return gameState.get();
    }
}
