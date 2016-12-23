package ru.spbau.blackout.serverside.multiplayer;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.network.AndroidClient.AbilityCast;
import ru.spbau.blackout.network.Events;
import ru.spbau.blackout.network.GameState;
import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.serverside.servers.RoomServer;
import ru.spbau.blackout.sessionsettings.SessionSettings;
import ru.spbau.blackout.settings.GameSettings;
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
                    final GameUnit clientUnit = (GameUnit) gameWorld.getObjectById(clientThread.getPlayerUid());

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
    public boolean hasUI() {
        return false;
    }

    @Override
    public GameScreen getScreen() {
        return null;
    }

    GameState getGameState() {
        return gameState;
    }

    private void createRoomAndSendItToClients() throws IOException {

        SessionSettings sessionSettings = SessionSettings.getTest();
        gameWorld = new ServerGameWorld(sessionSettings.getDefinitions(), this);
        sessionSettings.initializeGameWorld();

        for (int i = 0; i < clients.size(); i++) {
            final ClientThread client = clients.get(i);
            client.setGame(this, sessionSettings, i + 1);
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
