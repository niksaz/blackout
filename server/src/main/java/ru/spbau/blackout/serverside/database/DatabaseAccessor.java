package ru.spbau.blackout.serverside.database;

import com.mongodb.MongoClient;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import ru.spbau.blackout.database.Database;
import ru.spbau.blackout.database.PlayerProfile;

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

    public <T> Query<PlayerProfile> queryProfile(String name) {
        return instance
                .getDatastore()
                .createQuery(PlayerProfile.class)
                .field("name")
                .equal(name);
    }

    public <T> UpdateResults performUpdate(Query<T> query, UpdateOperations<T> updateOperations) {
        return instance
                .getDatastore()
                .update(query, updateOperations);
    }
}
