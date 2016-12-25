package ru.spbau.blackout.serverside.servers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

import ru.spbau.blackout.database.PlayerProfile;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.serverside.database.DatabaseAccessor;

import static ru.spbau.blackout.database.Database.ABILITY_UPGRADE;
import static ru.spbau.blackout.database.Database.ABILITY_UPGRADE_COST;
import static ru.spbau.blackout.database.Database.GOLD_CHANGE;
import static ru.spbau.blackout.database.Database.HEALTH_UPGRADE;
import static ru.spbau.blackout.database.Database.HEALTH_UPGRADE_COST;
import static ru.spbau.blackout.database.Database.HEALTH_UPGRADE_PER_LEVEL;

/**
 * Handler for updateState queries from clients.
 * Assumes that client have logged in before so his entry exists in the database.
 */
public class UpgradeRequestHandler implements HttpHandler {

    private final HttpRequestServer server;

    public UpgradeRequestHandler(HttpRequestServer server) {
        this.server = server;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (
            InputStream input = exchange.getRequestBody();
            DataInputStream inputStream = new DataInputStream(input)
        ) {
            final String name = inputStream.readUTF();
            final Query<PlayerProfile> query =
                    DatabaseAccessor.getInstance().getDatastore()
                            .createQuery(PlayerProfile.class)
                            .field("name")
                            .equal(name);
            final List<PlayerProfile> result = query.asList();
            if (result.size() != 1) {
                throw new IllegalStateException();
            }

            final PlayerProfile playerProfile = result.get(0);
            final Character.Definition definition = playerProfile.getCharacterDefinition();

            final String characteristic = inputStream.readUTF();
            boolean successful;
            switch (characteristic) {
                case GOLD_CHANGE:
                    final int delta = inputStream.readInt();
                    if (playerProfile.getCurrentCoins() + delta >= 0) {
                        performDatabaseUpdate(query, generateUpdateOperations(delta, definition));
                        successful = true;
                    } else {
                        successful = false;
                    }
                    break;

                case HEALTH_UPGRADE:
                    if (playerProfile.getCurrentCoins() >= HEALTH_UPGRADE_COST) {
                        definition.maxHealth += HEALTH_UPGRADE_PER_LEVEL;
                        performDatabaseUpdate(query, generateUpdateOperations(-HEALTH_UPGRADE_COST, definition));
                        successful = true;
                    } else {
                        successful = false;
                    }
                    break;

                case ABILITY_UPGRADE:
                    final int abilityIndex = inputStream.readInt();
                    if (playerProfile.getCurrentCoins() >= ABILITY_UPGRADE_COST) {
                        definition.abilities[abilityIndex].increaseLevel();
                        performDatabaseUpdate(query, generateUpdateOperations(-ABILITY_UPGRADE_COST, definition));
                        successful = true;
                    } else {
                        successful = false;
                    }
                    break;

                default:
                    successful = false;
                    break;
            }

            if (successful) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                server.log("Successful " + characteristic + " UPGRADE FOR " + name);
            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_FORBIDDEN, 0);
                server.log("Unsuccessful " + characteristic + " UPGRADE FOR " + name + "!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static UpdateOperations<PlayerProfile> generateUpdateOperations(int goldCost, Character.Definition newDefinition) {
        return DatabaseAccessor.getInstance().getDatastore()
                .createUpdateOperations(PlayerProfile.class)
                .inc("currentCoins", goldCost)
                .set("serializedDefinition", newDefinition.serializeToByteArray());
    }

    private static <T> UpdateResults performDatabaseUpdate(Query<T> query, UpdateOperations<T> updateOperations) {
        return DatabaseAccessor.getInstance().getDatastore().update(query, updateOperations);
    }
}
