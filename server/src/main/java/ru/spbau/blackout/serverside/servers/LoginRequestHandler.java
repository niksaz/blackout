package ru.spbau.blackout.serverside.servers;

import com.mongodb.MongoClient;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;

import ru.spbau.blackout.database.Database;
import ru.spbau.blackout.database.PlayerEntity;

public class LoginRequestHandler implements HttpHandler {

    private final HttpRequestServer server;
    private final Datastore datastore;

    public LoginRequestHandler(HttpRequestServer server) {
        this.server = server;
        final Morphia morphia = new Morphia();
        morphia.map(PlayerEntity.class);
        datastore = morphia.createDatastore(new MongoClient(), Database.DATABASE_NAME);
        datastore.ensureIndexes();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (
             InputStream input = exchange.getRequestBody();
             DataInputStream inputStream = new DataInputStream(input);
             OutputStream outputStream = exchange.getResponseBody()
        ) {
            final String name = inputStream.readUTF();

            final Query<PlayerEntity> queryResult =
                    datastore.createQuery(PlayerEntity.class).field("name").equal(name);
            final List<PlayerEntity> result = queryResult.asList();

            PlayerEntity entity;
            switch (result.size()) {
                case 0:
                    entity = new PlayerEntity(name);
                    datastore.save(entity);
                    break;

                case 1:
                    entity = result.get(0);
                    break;

                default:
                    throw new IllegalStateException();
            }

            final String response =
                    "Hello, " + entity.getName() + "! Your gold: " + entity.getGold();
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes().length);
            outputStream.write(response.getBytes());
            server.log(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
