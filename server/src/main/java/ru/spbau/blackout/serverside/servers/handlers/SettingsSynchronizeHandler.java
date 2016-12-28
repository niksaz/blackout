package ru.spbau.blackout.serverside.servers.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;

import ru.spbau.blackout.database.PlayerProfile;
import ru.spbau.blackout.serverside.database.DatabaseAccessor;
import ru.spbau.blackout.serverside.servers.HttpRequestServer;

/**
 * A client sends this type of requests when he needs to synchronize settings because of local changes.
 */
public class SettingsSynchronizeHandler implements HttpHandler {

    private final HttpRequestServer server;

    public SettingsSynchronizeHandler(HttpRequestServer server) {
        this.server = server;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (InputStream input = exchange.getRequestBody();
             ObjectInputStream inputStream = new ObjectInputStream(input)
        ) {
            final String name = inputStream.readUTF();
            final byte[] newSettings = (byte[]) inputStream.readObject();

            final Query<PlayerProfile> query = DatabaseAccessor.getInstance().queryProfile(name);
            if (query.asList().size() != 1) {
                throw new IllegalStateException();
            }

            final UpdateOperations<PlayerProfile> updateOperations =
                    DatabaseAccessor.getInstance().getDatastore()
                        .createUpdateOperations(PlayerProfile.class)
                        .set("serializedSettings", newSettings);
            DatabaseAccessor.getInstance().performUpdate(query, updateOperations);

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            server.log("Synchronized settings for " + name);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
