package ru.spbau.blackout.serverside.servers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.mongodb.morphia.query.Query;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.util.List;

import ru.spbau.blackout.database.PlayerEntity;
import ru.spbau.blackout.serverside.database.DatabaseAccessor;

/**
 * Handler for load queries from clients.
 * Checks whether this client exists and if he doesn't exist the creates a new account.
 */
public class LoadRequestHandler implements HttpHandler {

    private final HttpRequestServer server;

    public LoadRequestHandler(HttpRequestServer server) {
        this.server = server;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (
            InputStream input = exchange.getRequestBody();
            DataInputStream inputStream = new DataInputStream(input);
        ) {
            final String name = inputStream.readUTF();

            final Query<PlayerEntity> query =
                    DatabaseAccessor.getInstance().getDatastore()
                            .createQuery(PlayerEntity.class)
                            .field("name")
                            .equal(name);
            final List<PlayerEntity> result = query.asList();

            final PlayerEntity entity;
            switch (result.size()) {
                case 0:
                    System.out.println("COULD NOT FIND");
                    entity = new PlayerEntity(name);
                    DatabaseAccessor.getInstance().getDatastore().save(entity);
                    break;

                case 1:
                    entity = result.get(0);
                    break;

                default:
                    throw new IllegalStateException();
            }

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(exchange.getResponseBody())
            ) {
                objectOutputStream.writeObject(entity);
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.log("Sent: " + entity.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
