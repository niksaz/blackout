package ru.spbau.blackout.serverside.multiplayer;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;

import org.mongodb.morphia.query.Query;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.database.PlayerEntity;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.Decoration;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.game_session.TestingSessionSettings;
import ru.spbau.blackout.java8features.Optional;
import ru.spbau.blackout.network.AndroidClient.AbilityCast;
import ru.spbau.blackout.network.Events;
import ru.spbau.blackout.network.GameState;
import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.serverside.database.DatabaseAccessor;
import ru.spbau.blackout.serverside.servers.RoomServer;
import ru.spbau.blackout.settings.GameSettings;
import ru.spbau.blackout.shapescreators.CircleCreator;
import ru.spbau.blackout.utils.Utils;
import ru.spbau.blackout.worlds.GameWorld;
import ru.spbau.blackout.worlds.ServerGameWorld;

/**
 * Multiplayer game representation. Used for synchronizing game's state and watching for game flow, i.e. if someone
 * disconnects it will finish the game. It serializes the current world and asks ClientThreads to send it
 * to devices. ClientThreads pass user inputs from devices.
 */
public class Game extends Thread implements GameContext {

    private final int gameId;
    private final RoomServer server;
    private final List<ClientThread> clients;
    private ServerGameWorld gameWorld;
    private volatile GameState gameState = GameState.READY_TO_START;

    public Game(RoomServer server, List<ClientThread> clients, int gameId) {
        this.gameId = gameId;
        this.server = server;
        this.clients = clients;
    }

    public void run() {
        server.log("New game with id #" + gameId + " is going to run!");
        try {
            createRoomAndSendItToClients();
            waitWhileEveryoneIsReady();
        } catch (IOException e) {
            e.printStackTrace();
            gameState = GameState.FINISHED;
            return;
        }

        long timeLastIterationFinished = System.currentTimeMillis();
        long lastWorldUpdateTime = System.currentTimeMillis();
        while (gameState != GameState.FINISHED) {
            try {
                for (int clientIndex = 0; clientIndex < clients.size(); clientIndex++) {
                    final ClientThread clientThread = clients.get(clientIndex);
                    final GameUnit clientUnit = (GameUnit) gameWorld.getGameObjects().get(clientIndex);

                    final Vector2 heroVelocity = clientThread.getVelocityFromClient();
                    if (heroVelocity != null) {
                        clientUnit.setSelfVelocity(heroVelocity);
                    }
                    final AbilityCast abilityCast = clientThread.getAbilityCastFromClient();
                    if (abilityCast != null) {
                        Events.abilityCast(clientUnit, abilityCast.abilityNum, abilityCast.target);
                    }
                }

                long currentTime = System.currentTimeMillis();
                final float worldDeltaInSecs = (currentTime - lastWorldUpdateTime) / Utils.MILLIS_IN_SECOND;
                gameWorld.update(worldDeltaInSecs);
                lastWorldUpdateTime = currentTime;
                server.log("Updating gameWorld: " + worldDeltaInSecs);

                final byte[] worldInBytes = serializeWorld();
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

    private void createRoomAndSendItToClients() throws IOException {
        final TestingSessionSettings room = new TestingSessionSettings();
        room.map = "maps/duel/duel.g3db";

        final List<Character.Definition> heroes = new ArrayList<>();
        for (ClientThread client : clients) {
            final Query<PlayerEntity> query =
                    DatabaseAccessor.getInstance().getDatastore()
                            .createQuery(PlayerEntity.class)
                            .field("name")
                            .equal(client.getClientName());
            final List<PlayerEntity> result = query.asList();
            if (result.size() != 1) {
                throw new IllegalStateException();
            }
            final PlayerEntity entity = result.get(0);

            try (
                    ByteArrayInputStream byteInput = new ByteArrayInputStream(entity.getSerializedDefinition());
                    ObjectInputStream in = new ObjectInputStream(byteInput)
            ) {
                final Character.Definition hero = (Character.Definition) in.readObject();
                hero.overHeadPivotOffset.set(0, 0, 3.5f);
                room.objectDefs.add(hero);
                heroes.add(hero);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                throw new IllegalStateException(e);
            }
        }

        final GameObject.Definition stone = new Decoration.Definition(
                "models/stone/stone.g3db",
                new CircleCreator(1.1f),
                0, -20
        );
        room.objectDefs.add(stone);

        for (GameObject.Definition def : room.getDefintions()) {
            def.setContextOnServer(this);
        }

        // FIXME: just for test
        final List<GameObject> heroesObjects = new ArrayList<>();
        heroesObjects.add(room.getDefintions().get(0).makeInstanceWithNextUid(0, 0));
        heroesObjects.add(room.getDefintions().get(1).makeInstanceWithNextUid(5, 5));
        room.getDefintions().get(2).makeInstanceWithNextUid(0, -10);

        gameWorld = new ServerGameWorld(room.objectDefs);

        final byte[] worldInBytes = serializeWorld();

        for (int i = 0; i < clients.size(); i++) {
            final ClientThread client = clients.get(i);
            client.setWorldToSend(worldInBytes);
            client.setGame(this, room, heroes.get(i), heroesObjects.get(i).getUid());
        }
    }

    private byte[] serializeWorld() throws IOException {
        final ByteArrayOutputStream serializedVersionOfWorld = new ByteArrayOutputStream();
        final ObjectOutputStream objectOutputStreamForWorld = new ObjectOutputStream(serializedVersionOfWorld);
        try {
            gameWorld.getState(objectOutputStreamForWorld);
            objectOutputStreamForWorld.flush();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            gameState = GameState.FINISHED;
        }
        return serializedVersionOfWorld.toByteArray();
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
