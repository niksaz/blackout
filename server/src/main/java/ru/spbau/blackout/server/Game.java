package ru.spbau.blackout.server;

import com.badlogic.gdx.math.Vector2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ru.spbau.blackout.GameWorld;
import ru.spbau.blackout.entities.Decoration;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.gamesession.TestingSessionSettings;
import ru.spbau.blackout.network.GameState;
import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.shapescreators.CircleCreator;
import ru.spbau.blackout.utils.Utils;

/**
 * Multiplayer game representation. Used for synchronizing game's state and watching for game flow,
 * i.e. if someone disconnects it will finish the game.
 */
class Game extends Thread {

    private static AtomicInteger gamesCreated = new AtomicInteger();

    private final int gameId;
    private final RoomServer server;
    private final List<ClientThread> clients;
    private final GameWorld gameWorld = new GameWorld();
    private volatile GameState gameState = GameState.READY_TO_START;

    Game(RoomServer server, List<ClientThread> clients) {
        this.gameId = gamesCreated.getAndAdd(1);
        this.server = server;
        this.clients = clients;
    }

    public void run() {
        server.log("New game with id #" + gameId + " is going to start!");

        final TestingSessionSettings room = new TestingSessionSettings();
        room.map =  "maps/duel/duel.g3db";

        final List<Hero.Definition> heroes = new ArrayList<>();
        heroes.add(
                new Hero.Definition(
                "models/wizard/wizard.g3db",
                new CircleCreator(0.7f),
                0, 0));
        heroes.add(
                new Hero.Definition(
                "models/wizard/wizard.g3db",
                new CircleCreator(0.7f),
                5, 5));
        room.objectDefs.addAll(heroes);

        GameObject.Definition stone = new Decoration.Definition(
                "models/stone/stone.g3db",
                new CircleCreator(1.5f),
                0, -20
        );
        room.objectDefs.add(stone);

        for (GameObject.Definition def : room.getObjectDefs()) {
            def.makeInstance(null, gameWorld);
        }

        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).setGame(this, i, room, heroes.get(i));
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

        if (gameState == GameState.FINISHED) {
            return;
        }
        gameState = GameState.IN_PROCESS;

        // creating streams for deserialization of the world for further usage for ClientThreads
        long timeLastIterationFinished = System.currentTimeMillis();
        long timeLastWorldUpdate = System.currentTimeMillis();
        while (gameState != GameState.FINISHED) {
            try (
                ByteArrayOutputStream serializedVersionOfWorld = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStreamForWorld = new ObjectOutputStream(serializedVersionOfWorld)
            ) {
                long currentTime;
                synchronized (gameWorld) {
                    currentTime = System.currentTimeMillis();
                    gameWorld.update((currentTime - timeLastWorldUpdate) / Utils.MILLIS_IN_SECOND);
                    server.log("Updating gameWorld: " + (currentTime - timeLastWorldUpdate) / Utils.MILLIS_IN_SECOND);
                    try {
                        gameWorld.inplaceSerialize(objectOutputStreamForWorld);
                        objectOutputStreamForWorld.flush();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                timeLastWorldUpdate = currentTime;

                final byte[] worldInBytes = serializedVersionOfWorld.toByteArray();
                if (worldInBytes.length != 0) {
                    for (ClientThread client : clients) {
                        // watching client's states
                        if (client.getClientGameState() == GameState.FINISHED) {
                            gameState = GameState.FINISHED;
                            break;
                        }
                        client.setWorldToSend(worldInBytes);
                    }
                }

                //making sure that we are sending position with desired rate
                final long duration = timeLastIterationFinished - System.currentTimeMillis();
                if (duration < Network.TIME_SHOULD_BE_SPENT_FOR_ITERATION) {
                    try {
                        sleep(Network.TIME_SHOULD_BE_SPENT_FOR_ITERATION - duration);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        gameState = GameState.FINISHED;
                    }
                }
                timeLastIterationFinished = System.currentTimeMillis();
            } catch (IOException e) {
                e.printStackTrace();
                gameState = GameState.FINISHED;
            }
        }
    }

    void setVelocityFor(int numberInArray, Vector2 newVelocity) {
        server.log("Setting velocity for " + numberInArray + " " + newVelocity);
        synchronized (gameWorld) {
            GameUnit object = (GameUnit) gameWorld.getGameObjects().get(numberInArray);
            object.setSelfVelocity(newVelocity);
        }
    }

    GameState getGameState() {
        return gameState;
    }

    GameWorld getGameWorld() {
        return gameWorld;
    }
}
