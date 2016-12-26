package ru.spbau.blackout.serverside.multiplayer;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.database.Database;
import ru.spbau.blackout.database.PlayerProfile;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.network.AndroidClient.AbilityCast;
import ru.spbau.blackout.network.Events;
import ru.spbau.blackout.network.GameState;
import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.serverside.database.DatabaseAccessor;
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
        boolean someoneWon = false;
        while (gameState != GameState.FINISHED) {
            try {
                ClientThread clientThreadWithAliveCharacter = null;
                int aliveClients = 0;
                int aliveCharacters = 0;
                for (ClientThread clientThread : clients) {
                    final Character clientCharacter = (Character) gameWorld.getObjectById(clientThread.getPlayerUid());
                    if (clientCharacter != null) {
                        if (clientThread.getClientGameState() == GameState.FINISHED) {
                            clientCharacter.kill();
                        } else {
                            aliveClients += 1;
                            aliveCharacters += 1;
                            clientThreadWithAliveCharacter = clientThread;
                            final Vector2 characterVelocity = clientThread.getVelocityFromClient();
                            if (characterVelocity != null) {
                                Events.setSelfVelocity(clientCharacter, characterVelocity);
                            }
                            final AbilityCast abilityCast = clientThread.getAbilityCastFromClient();
                            if (abilityCast != null) {
                                Events.abilityCast(clientCharacter, abilityCast.abilityNum, abilityCast.target);
                            }
                        }
                    } else {
                        if (clientThread.getClientGameState() != GameState.FINISHED) {
                            aliveClients += 1;
                        }
                    }
                }

                if (aliveClients == 0) {
                    gameState = GameState.FINISHED;
                    break;
                }

                long currentTime = System.currentTimeMillis();
                final float worldDeltaInSecs = (currentTime - lastWorldUpdateTime) / Utils.MILLIS_IN_SECOND;
                gameWorld.updatePhysics(worldDeltaInSecs);
                lastWorldUpdateTime = currentTime;
                server.log("Updating gameWorld: " + worldDeltaInSecs);

                final byte[] worldInBytes = serializeWorld();
                System.out.println("World size is " + worldInBytes.length);
                for (ClientThread client : clients) {
                    if (client.getClientGameState() != GameState.FINISHED) {
                        client.setWorldToSend(worldInBytes);
                        if (aliveCharacters == 1 && !someoneWon) {
                            client.setWinnerName(clientThreadWithAliveCharacter.getClientName());
                        }
                    }
                }

                if (aliveCharacters == 1 && !someoneWon) {
                    someoneWon = true;
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
    public boolean hasUI() {
        return false;
    }

    @Override public AssetManager getAssets() { return null; }
    @Override public GameScreen getScreen() { return null; }
    @Override public ParticleSystem getParticleSystem() { return null; }
    @Override public Character getMainCharacter() { return null; }

    GameState getGameState() {
        return gameState;
    }

    private void createRoomAndSendItToClients() throws IOException {
        final Array<Character.Definition> heroes = new Array<>();
        for (ClientThread client : clients) {

            if (client.getClientName().equals(ClientThread.UNKNOWN)) {
                //noinspection SynchronizationOnLocalVariableOrMethodParameter
                synchronized (client) {
                    if (client.getClientName().equals(ClientThread.UNKNOWN)) {
                        try {
                            client.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            interrupt();
                        }
                    }
                }
            }

            final Query<PlayerProfile> query = DatabaseAccessor.getInstance().queryProfile(client.getClientName());
            final List<PlayerProfile> result = query.asList();
            if (result.size() != 1) {
                throw new IllegalStateException();
            }
            final PlayerProfile playerProfile = result.get(0);
            heroes.add(playerProfile.getCharacterDefinition());
        }

        SessionSettings sessionSettings = SessionSettings.createDefaultSession(heroes);
        gameWorld = new ServerGameWorld(sessionSettings, this);
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
