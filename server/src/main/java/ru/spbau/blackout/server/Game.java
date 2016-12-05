package ru.spbau.blackout.server;

import com.badlogic.gdx.math.Vector2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import ru.spbau.blackout.entities.Decoration;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.gamesession.TestingSessionSettings;
import ru.spbau.blackout.network.GameState;
import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.shapescreators.CircleCreator;
import ru.spbau.blackout.utils.Utils;
import ru.spbau.blackout.worlds.GameWorldWithPhysics;

/**
 * Multiplayer game representation. Used for synchronizing game's state and watching for game flow, i.e. if someone
 * disconnects it will finish the game. It serializes the current world and asks ClientThreads to send it
 * to devices. ClientThreads pass user inputs from devices.
 */
class Game extends Thread {

    private final int gameId;
    private final RoomServer server;
    private final List<ClientThread> clients;
    private final GameWorldWithPhysics gameWorld = new GameWorldWithPhysics();
    private volatile GameState gameState = GameState.READY_TO_START;

    Game(RoomServer server, List<ClientThread> clients, int gameId) {
        this.gameId = gameId;
        this.server = server;
        this.clients = clients;
    }

    public void run() {
        server.log("New game with id #" + gameId + " is going to start!");
        createRoomAndSendItToClients();
        waitWhileEveryoneIsReady();

        long timeLastIterationFinished = System.currentTimeMillis();
        long lastWorldUpdateTime = System.currentTimeMillis();
        while (gameState != GameState.FINISHED) {
            try (
                ByteArrayOutputStream serializedVersionOfWorld = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStreamForWorld = new ObjectOutputStream(serializedVersionOfWorld)
            ) {
                for (int clientIndex = 0; clientIndex < clients.size(); clientIndex++) {
                    final Vector2 heroVelocity = clients.get(clientIndex).getVelocityFromClient().getAndSet(null);
                    if (heroVelocity != null) {
                        ((GameUnit) gameWorld.getGameObjects().get(clientIndex)).setSelfVelocity(heroVelocity);
                    }
                }

                long currentTime = System.currentTimeMillis();
                final float worldDeltaInSecs = (currentTime - lastWorldUpdateTime) / Utils.MILLIS_IN_SECOND;
                gameWorld.update(worldDeltaInSecs);
                lastWorldUpdateTime = currentTime;
                server.log("Updating gameWorld: " + worldDeltaInSecs);
                try {
                    gameWorld.inplaceSerialize(objectOutputStreamForWorld);
                    objectOutputStreamForWorld.flush();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    gameState = GameState.FINISHED;
                }

                final byte[] worldInBytes = serializedVersionOfWorld.toByteArray();
                for (ClientThread client : clients) {
                    if (client.getClientGameState() == GameState.FINISHED) {
                        gameState = GameState.FINISHED;
                        break;
                    }
                    client.setWorldToSend(worldInBytes);
                }

                final long duration = timeLastIterationFinished - System.currentTimeMillis();
                if (duration < Network.TIME_SHOULD_BE_SPENT_IN_ITERATION) {
                    try {
                        sleep(Network.TIME_SHOULD_BE_SPENT_IN_ITERATION - duration);
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

    GameState getGameState() {
        return gameState;
    }

    private void createRoomAndSendItToClients() {
        final TestingSessionSettings room = new TestingSessionSettings();
        room.map = "maps/duel/duel.g3db";

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

        final GameObject.Definition stone = new Decoration.Definition(
                "models/stone/stone.g3db",
                new CircleCreator(1.5f),
                0, -20
        );
        room.objectDefs.add(stone);

        for (GameObject.Definition gameObjectDef : room.getObjectDefs()) {
            gameObjectDef.makeInstance(null, gameWorld);
        }

        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).setGame(this, room, heroes.get(i));
        }
    }

    private void waitWhileEveryoneIsReady() {
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

        if (gameState != GameState.FINISHED) {
            gameState = GameState.IN_PROCESS;
        }
    }
}
