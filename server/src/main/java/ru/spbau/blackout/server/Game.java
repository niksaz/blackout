package ru.spbau.blackout.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import ru.spbau.blackout.GameWorld;
import ru.spbau.blackout.entities.Decoration;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.gamesession.TestingSessionSettings;
import ru.spbau.blackout.network.GameState;
import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.shapescreators.CircleCreator;

/**
 * Multiplayer game representation. Used for synchronizing game's state and watching for game flow,
 * i.e. if someone disconnects it will finish the game.
 */
class Game extends Thread {

    private static AtomicInteger gamesCreated = new AtomicInteger();

    private final int gameId;
    private final RoomServer server;
    private final List<ClientThread> clients;
    private final AtomicReference<GameState> gameState = new AtomicReference<>(GameState.READY_TO_START);
    private GameWorld gameWorld;

    Game(RoomServer server, List<ClientThread> clients) {
        gameId = gamesCreated.getAndAdd(1);
        this.server = server;
        this.clients = clients;
    }

    public void run() {
        server.log("New game with id #" + gameId + " has just started!");
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).setGame(this, i);
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

        final TestingSessionSettings room = new TestingSessionSettings();
        room.map =  "maps/duel/duel.g3db";

        Hero.Definition hero = new Hero.Definition(
                "models/wizard/wizard.g3db",
                new CircleCreator(0.7f),
                0, 0
        );

        Hero.Definition hero2 = new Hero.Definition(
                "models/wizard/wizard.g3db",
                new CircleCreator(0.7f),
                5, 5
        );

        room.objectDefs.add(hero);
        room.objectDefs.add(hero2);

        GameObject.Definition stone = new Decoration.Definition(
                "models/stone/stone.g3db",
                new CircleCreator(1.5f),
                0, -20
        );
        room.objectDefs.add(stone);

        gameWorld = new GameWorld();
        for (GameObject.Definition def : room.getObjectDefs()) {
            def.makeInstance(null, gameWorld);
        }

        clients.get(0).setSessionSettings(room, hero);
        clients.get(1).setSessionSettings(room, hero2);

        // !!!!!!!

        long lastTime = System.currentTimeMillis();
        while (true) {
            long currentTime;
            //noinspection SynchronizeOnNonFinalField
            synchronized (gameWorld) {
                currentTime = System.currentTimeMillis();
                gameWorld.update(currentTime - lastTime);
                server.log("Updating gameWorld: " + Long.valueOf(currentTime - lastTime).toString());
            }
            lastTime = currentTime;
            try {
                sleep(Network.FRAMES_60_SLEEP_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //float currentTime = Gdx.graphics.getDeltaTime();
            //server.log("Time past: " + currentTime);
        }


//        gameState.set(GameState.IN_PROCESS);
//
//        do {
//            for (ClientThread thread : clients) {
//                GameState currentClientGameState = thread.getClientGameState();
//                if (currentClientGameState == GameState.FINISHED) {
//                    server.log(thread.getClientName() +
//                               " disconnected. Game with id #" + gameId + " will be finished.");
//                    gameState.set(GameState.FINISHED);
//                    break;
//                }
//            }
//        } while (gameState.get() != GameState.FINISHED);
    }

    public void setVelocityFor(int numberInArray, Vector2 newVelocity) {
        //noinspection SynchronizeOnNonFinalField
        synchronized (gameWorld) {
            server.log("Setting velocity for " + numberInArray + " " + newVelocity);
            GameUnit object = (GameUnit) gameWorld.getGameObjects().get(numberInArray);
            object.setSelfVelocity(newVelocity);
        }
    }

    GameState getGameState() {
        return gameState.get();
    }

    public GameWorld getGameWorld() {
        //noinspection SynchronizeOnNonFinalField
        return gameWorld;
    }
}
