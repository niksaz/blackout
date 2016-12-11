package ru.spbau.blackout.serverside.servers;

import com.mongodb.MongoClient;
import com.sun.net.httpserver.HttpServer;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.PrintStream;
import java.net.InetSocketAddress;

import ru.spbau.blackout.database.Database;
import ru.spbau.blackout.database.PlayerEntity;

/**
 * Server for responding of loading and update of player's information. Stores connection to MongoDB database.
 */
public class HttpRequestServer extends ServerWithLogging {

    private final Datastore datastore;

    public HttpRequestServer(int port, PrintStream logger, String tag) {
        super(port, logger, tag);
        final Morphia morphia = new Morphia();
        morphia.map(PlayerEntity.class);
        datastore = morphia.createDatastore(new MongoClient(), Database.DATABASE_NAME);
        datastore.ensureIndexes();
    }

    public void start() {
        try {
            final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext(Database.LOAD_COMMAND, new LoadRequestHandler(this));
            server.createContext(Database.UPDATE_COMMAND, new UpdateRequestHandler(this));
            server.setExecutor(null);
            server.start();
            log("Server started.");
        } catch (Exception e) {
            log("Exception while creating HttpServer on port " + port + ":" + e.getMessage());
        }
    }

    public Datastore getDatastore() {
        return datastore;
    }
}
