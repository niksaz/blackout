package ru.spbau.blackout.serverside.multiplayer;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.game_session.TestingSessionSettings;
import ru.spbau.blackout.java8features.Optional;
import ru.spbau.blackout.network.GameState;
import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.serverside.servers.RoomServer;
import ru.spbau.blackout.settings.GameSettings;
import ru.spbau.blackout.utils.Utils;
import ru.spbau.blackout.worlds.GameWorld;
import ru.spbau.blackout.worlds.GameWorldWithPhysics;

/**
 * Multiplayer game representation. Used for synchronizing game's state and watching for game flow, i.e. if someone
 * disconnects it will finish the game. It serializes the current world and asks ClientThreads to send it
 * to devices. ClientThreads pass user inputs from devices.
 */
public class Game extends Thread implements GameContext {

    private final int gameId;
    private final RoomServer server;
    private final List<RoomClientThread> clients;
    private final GameWorldWithPhysics gameWorld = new GameWorldWithPhysics();
    private volatile GameState gameState = GameState.READY_TO_START;

    public Game(RoomServer server, List<RoomClientThread> clients, int gameId) {
        this.gameId = gameId;
        this.server = server;
        this.clients = clients;
    }


    public void run() {
        server.log("New game with id #" + gameId + " is going to run!");
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
                for (RoomClientThread client : clients) {
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

    @Override
    public GameSettings getSettings() {
        return null;
    }

    @Override
    public GameWorld gameWorld() {
        return gameWorld;
    }

    @Override
    public AssetManager getAssets() {
        return null;
    }

    @Override
    public Optional<AssetManager> assets() {
        return Optional.ofNullable(this.getAssets());
    }

    @Override
    public Optional<GameSettings> settings() {
        return Optional.ofNullable(this.getSettings());
    }

    @Override
    public boolean hasIO() {
        return false;
    }

    GameState getGameState() {
        return gameState;
    }

    private void createRoomAndSendItToClients() {
        final TestingSessionSettings room = TestingSessionSettings.getTest();

        final List<Character.Definition> heroes = new ArrayList<>();
        heroes.add((Character.Definition) room.objectDefs.get(0));
        heroes.add((Character.Definition) room.objectDefs.get(1));

        for (GameObject.Definition def : room.getObjectDefs()) {
            def.setContextOnServer(this);
            def.makeInstance();
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
                for (RoomClientThread thread : clients) {
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
