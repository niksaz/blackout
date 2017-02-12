package ru.spbau.blackout.serverside.database;

import com.mongodb.MongoClient;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

import ru.spbau.blackout.database.Database;
import ru.spbau.blackout.database.PlayerProfile;
import ru.spbau.blackout.entities.Character;

import static ru.spbau.blackout.database.Database.ABILITY_UPGRADE;
import static ru.spbau.blackout.database.Database.ABILITY_UPGRADE_COST;
import static ru.spbau.blackout.database.Database.COINS_EARNED;
import static ru.spbau.blackout.database.Database.HEALTH_UPGRADE;
import static ru.spbau.blackout.database.Database.HEALTH_UPGRADE_COST;
import static ru.spbau.blackout.database.Database.HEALTH_UPGRADE_PER_LEVEL;

/**
 * Unified access to MongoDB database from the server module.
 */
public class DatabaseAccessor {

    private static final DatabaseAccessor instance = new DatabaseAccessor();

    private Datastore datastore;

    private DatabaseAccessor() {
        final Morphia morphia = new Morphia();
        morphia.map(PlayerProfile.class);
        datastore = morphia.createDatastore(new MongoClient(), Database.DATABASE_NAME);
        datastore.ensureIndexes();
    }

    public static DatabaseAccessor getInstance() {
        return instance;
    }

    public Datastore getDatastore() {
        return datastore;
    }

    public Query<PlayerProfile> queryProfile(String name) {
        return getDatastore()
                .createQuery(PlayerProfile.class)
                .field("name")
                .equal(name);
    }

        public <T> UpdateResults performUpdate(Query<T> query, UpdateOperations<T> updateOperations) {
        return getDatastore()
                .update(query, updateOperations);
    }

    public boolean handleUpdateFromInputStream(DataInputStream inputStream) throws IOException {
        final String name = inputStream.readUTF();
        final Query<PlayerProfile> query = queryProfile(name);
        final List<PlayerProfile> result = query.asList();
        if (result.size() != 1) {
            throw new IllegalStateException();
        }

        final PlayerProfile playerProfile = result.get(0);
        final Character.Definition definition = playerProfile.getCharacterDefinition();

        final String characteristic = inputStream.readUTF();
        boolean successful;
        switch (characteristic) {
            case COINS_EARNED:
                final int delta = inputStream.readInt();
                final UpdateOperations<PlayerProfile> updateOperations =
                        getDatastore()
                                .createUpdateOperations(PlayerProfile.class)
                                .inc("currentCoins", delta)
                                .inc("earnedCoins", delta);
                performUpdate(query, updateOperations);
                successful = true;
                break;

            case HEALTH_UPGRADE:
                if (playerProfile.getCurrentCoins() >= HEALTH_UPGRADE_COST) {
                    definition.maxHealth += HEALTH_UPGRADE_PER_LEVEL;
                    performUpdate(query, generateUpdateOperations(-HEALTH_UPGRADE_COST, definition));
                    successful = true;
                } else {
                    successful = false;
                }
                break;

            case ABILITY_UPGRADE:
                final int abilityIndex = inputStream.readInt();
                if (playerProfile.getCurrentCoins() >= ABILITY_UPGRADE_COST) {
                    definition.abilities[abilityIndex].increaseLevel();
                    performUpdate(query, generateUpdateOperations(-ABILITY_UPGRADE_COST, definition));
                    successful = true;
                } else {
                    successful = false;
                }
                break;

            default:
                successful = false;
                break;
        }
        return successful;
    }

    private UpdateOperations<PlayerProfile> generateUpdateOperations(int goldCost, Character.Definition newDefinition) {
        return getDatastore()
                .createUpdateOperations(PlayerProfile.class)
                .inc("currentCoins", goldCost)
                .set("serializedDefinition", newDefinition.serializeToByteArray());
    }
}
