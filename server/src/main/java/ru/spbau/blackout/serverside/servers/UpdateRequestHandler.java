package ru.spbau.blackout.serverside.servers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;

import ru.spbau.blackout.database.PlayerEntity;

/**
 * Handler for update queries from clients.
 * Assumes that client have logged in before so his entry exists in the database.
 */
public class UpdateRequestHandler implements HttpHandler {

    private final HttpRequestServer server;

    public UpdateRequestHandler(HttpRequestServer server) {
        this.server = server;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (
            InputStream input = exchange.getRequestBody();
            DataInputStream inputStream = new DataInputStream(input)
        ) {
            final String name = inputStream.readUTF();
            final int delta = inputStream.readInt();

            final Query<PlayerEntity> query =
                    server.getDatastore().createQuery(PlayerEntity.class).field("name").equal(name);
            final List<PlayerEntity> result = query.asList();

            if (result.size() != 1) {
                throw new IllegalStateException();
            }

            final PlayerEntity playerEntity = result.get(0);
            if (playerEntity.getGold() + delta >= 0) {
                final UpdateOperations<PlayerEntity> updateOperations =
                        server.getDatastore().createUpdateOperations(PlayerEntity.class).inc("gold", delta);
                server.getDatastore().update(query, updateOperations);

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                server.log("Updated gold for " + name);
            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_FORBIDDEN, 0);
                server.log("Denied updating of gold for " + name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
