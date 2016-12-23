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
import ru.spbau.blackout.serverside.database.DatabaseAccessor;

import static ru.spbau.blackout.database.Database.GOLD_FIELD;

/**
 * Handler for update queries from clients.
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
            final Query<PlayerEntity> query =
                    DatabaseAccessor.getInstance().getDatastore()
                            .createQuery(PlayerEntity.class)
                            .field("name")
                            .equal(name);
            final List<PlayerEntity> result = query.asList();
            if (result.size() != 1) {
                throw new IllegalStateException();
            }
            final PlayerEntity playerEntity = result.get(0);

            final String characteristic = inputStream.readUTF();
            boolean successful;
            switch (characteristic) {
                case GOLD_FIELD:
                    final int delta = inputStream.readInt();
                    if (playerEntity.getGold() + delta >= 0) {
                        final UpdateOperations<PlayerEntity> updateOperations =
                                DatabaseAccessor.getInstance().getDatastore()
                                        .createUpdateOperations(PlayerEntity.class)
                                        .inc(GOLD_FIELD, delta);
                        DatabaseAccessor.getInstance().getDatastore().update(query, updateOperations);
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
}
