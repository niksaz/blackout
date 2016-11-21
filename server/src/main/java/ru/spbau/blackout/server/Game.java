package ru.spbau.blackout.server;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import ru.spbau.blackout.network.GameState;

/**
 * Multiplayer game representation. Used for synchronizing game's state and watching for game flow,
 * i.e. if someone disconnects it will finish the game.
 */
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
        synchronized (this) {
            boolean everyoneIsReady;
            do {
                everyoneIsReady = true;
                for (ClientThread thread : clients) {
                    final GameState currentClientGameState = thread.getClientGameState();
                    if (currentClientGameState == GameState.WAITING) {
                        everyoneIsReady = false;
                        break;
                    }
                }
                if (!everyoneIsReady) {
                    try {
                        this.wait();
                    } catch (InterruptedException ignored) {
                    }
                }
            } while (!everyoneIsReady);
        }

        if (gameState.get() == GameState.FINISHED) {
            return;
        }

        // !!!!!!

/*        final TestingSessionSettings room = new TestingSessionSettings();
        room.map =  "maps/duel/duel.g3db";

        final Shape heroShape = new CircleShape();
        heroShape.setRadius(0.7f);
        Hero.Definition hero = new Hero.Definition("models/wizard/wizard.g3db", heroShape, 0, 0);

        final Shape heroShape2 = new CircleShape();
        heroShape.setRadius(0.7f);
        Hero.Definition hero2 = new Hero.Definition("models/wizard/wizard.g3db", heroShape2, 50, 50);

        room.objectDefs.add(hero);
        room.objectDefs.add(hero2);

        final Shape stoneShape = new CircleShape();
        heroShape.setRadius(1.5f);
        final GameObject.Definition stone = new Decoration.Definition(
                "models/stone/stone.g3db", stoneShape, 0, -20
        );
        room.objectDefs.add(stone);

        final GameWorld gameWorld = new GameWorld();
        for (GameObject.Definition def : room.getObjectDefs()) {
            def.makeInstance(null, gameWorld);
        }

        clients.create(0).setSessionSettings(room, hero);
        clients.create(1).setSessionSettings(room, hero2);*/

        // !!!!!!!

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
