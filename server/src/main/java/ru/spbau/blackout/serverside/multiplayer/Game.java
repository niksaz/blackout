package ru.spbau.blackout.serverside.multiplayer;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.database.Database;
import ru.spbau.blackout.database.PlayerProfile;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.network.AndroidClient.AbilityCast;
import ru.spbau.blackout.network.Events;
import ru.spbau.blackout.network.GameState;
import ru.spbau.blackout.network.Network;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.serializationutils.EffectiveOutputStream;
import ru.spbau.blackout.serverside.database.DatabaseAccessor;
import ru.spbau.blackout.serverside.servers.RoomServer;
import ru.spbau.blackout.sessionsettings.SessionSettings;
import ru.spbau.blackout.settings.GameSettings;
import ru.spbau.blackout.utils.Uid;
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
    private final List<ClientHandler> clients;
    private ServerGameWorld gameWorld;
    private volatile GameState gameState = GameState.READY_TO_START;

    public Game(RoomServer server, List<ClientHandler> clients, int gameId) {
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
                ClientHandler clientHandlerWithAliveCharacter = null;
                int aliveClients = 0;
                int aliveCharacters = 0;
                for (ClientHandler clientHandler : clients) {
                    final Character clientCharacter = (Character) gameWorld.getObjectById(clientHandler.getPlayerUid());
                    if (clientCharacter != null) {
                        if (clientHandler.getClientGameState() == GameState.FINISHED) {
                            clientCharacter.kill();
                        } else {
                            aliveClients += 1;
                            aliveCharacters += 1;
                            clientHandlerWithAliveCharacter = clientHandler;
                            final Vector2 characterVelocity = clientHandler.getVelocityFromClient();
                            if (characterVelocity != null) {
                                Events.setSelfVelocity(clientCharacter, characterVelocity);
                            }
                            final AbilityCast abilityCast = clientHandler.getAbilityCastFromClient();
                            if (abilityCast != null) {
                                Events.abilityCast(clientCharacter, abilityCast.abilityNum, abilityCast.target);
                            }
                        }
                    } else {
                        if (clientHandler.getClientGameState() != GameState.FINISHED) {
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

                someoneWon = monitorWinningConditions(someoneWon, aliveCharacters, clientHandlerWithAliveCharacter);

                final byte[] worldInBytes = serializeWorld();
                System.out.println("World size is " + worldInBytes.length);
                for (ClientHandler client : clients) {
                    if (client.getClientGameState() != GameState.FINISHED) {
                        client.setWorldToSend(worldInBytes);
                    }
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

    private boolean monitorWinningConditions(boolean alreadyWon, int aliveCharacters, ClientHandler aliveClient)
        throws IOException {

        if (aliveCharacters == 1 && !alreadyWon) {
            final ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            final DataOutputStream dataOutput = new DataOutputStream(byteOutput);
            dataOutput.writeUTF(aliveClient.getClientName());
            dataOutput.writeUTF(Database.COINS_EARNED);
            dataOutput.writeInt(Database.COINS_PER_WIN);
            dataOutput.flush();

            final DataInputStream dataInput =
                    new DataInputStream(new ByteArrayInputStream(byteOutput.toByteArray()));
            DatabaseAccessor.getInstance().handleUpdateFromInputStream(dataInput);

            int numberOfPlayers = clients.size();

            // for testing purposes
            if (numberOfPlayers > 1) {
                final double[] rating = new double[numberOfPlayers];
                final double[] q = new double[numberOfPlayers];
                double sum = 0;
                for (int i = 0; i < numberOfPlayers; i++) {
                    rating[i] = clients.get(i).getPlayerProfile().getRating();
                    q[i] = Math.pow(10.0, rating[i] / 400);
                    sum += q[i];
                }

                for (int i = 0; i < numberOfPlayers; i++) {
                    final ClientHandler client = clients.get(i);
                    // Elo rating formula
                    final double expectedScore = q[i] / sum;
                    final double actualScore =
                            aliveClient.getClientName().equals(client.getClientName()) ? 1.0 : 0.0;

                    final double clientRatingsChange = 40 * (actualScore - expectedScore);

                    final Query<PlayerProfile> playerProfileQuery =
                            DatabaseAccessor.getInstance().queryProfile(client.getClientName());
                    final UpdateOperations<PlayerProfile> updateOperations =
                            DatabaseAccessor.getInstance().getDatastore()
                                    .createUpdateOperations(PlayerProfile.class)
                                    .inc("rating", clientRatingsChange);
                    DatabaseAccessor.getInstance().performUpdate(playerProfileQuery, updateOperations);
                }
            }

            for (ClientHandler client : clients) {
                if (client.getClientGameState() != GameState.FINISHED) {
                    client.setWinnerName(aliveClient.getClientName());
                }
            }
            alreadyWon = true;
        }
        return alreadyWon;
    }

    private void createRoomAndSendItToClients() throws IOException {
        final Array<Character.Definition> heroes = new Array<>();
        for (ClientHandler client : clients) {

            if (client.getClientName().equals(ClientHandler.UNKNOWN)) {
                //noinspection SynchronizationOnLocalVariableOrMethodParameter
                synchronized (client) {
                    if (client.getClientName().equals(ClientHandler.UNKNOWN)) {
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
            client.setPlayerProfile(playerProfile);
            heroes.add(playerProfile.getCharacterDefinition());
        }

        SessionSettings sessionSettings = SessionSettings.createDefaultSession(heroes);
        gameWorld = new ServerGameWorld(sessionSettings, this);
        sessionSettings.initializeGameWorld();

        for (int i = 0; i < clients.size(); i++) {
            final ClientHandler client = clients.get(i);
            client.setGame(this, sessionSettings, Uid.get(i + 1));
        }
    }

    private byte[] serializeWorld() throws IOException {
        final ByteArrayOutputStream worldByteStream = new ByteArrayOutputStream();
        final GZIPOutputStream worldGzipStream = new GZIPOutputStream(worldByteStream);
        final EffectiveOutputStream worldStream = new EffectiveOutputStream(worldGzipStream);
        gameWorld.getState(worldStream);
        worldGzipStream.finish();
        worldGzipStream.flush();
        return worldByteStream.toByteArray();
    }

    private void waitWhileEveryoneIsReady() {
        synchronized (this) {
            boolean everyoneIsReady;
            do {
                everyoneIsReady = true;
                for (ClientHandler thread : clients) {
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

        for (ClientHandler clientHandler : clients) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (clientHandler) {
                clientHandler.notify();
            }
        }

        if (gameState != GameState.FINISHED) {
            gameState = GameState.IN_PROCESS;
        }
    }
}
