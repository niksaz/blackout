package ru.spbau.blackout.serverside.servers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.mongodb.morphia.query.Query;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;

import ru.spbau.blackout.database.PlayerEntity;

public class LoadRequestHandler implements HttpHandler {

    private final HttpRequestServer server;

    public LoadRequestHandler(HttpRequestServer server) {
        this.server = server;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (
            InputStream input = exchange.getRequestBody();
            DataInputStream inputStream = new DataInputStream(input);
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);
            OutputStream outputStream = exchange.getResponseBody()
        ) {
            final String name = inputStream.readUTF();

            final Query<PlayerEntity> query =
                    server.getDatastore().createQuery(PlayerEntity.class).field("name").equal(name);
            final List<PlayerEntity> result = query.asList();

            PlayerEntity entity;
            switch (result.size()) {
                case 0:
                    entity = new PlayerEntity(name);
                    server.getDatastore().save(entity);
                    break;

                case 1:
                    entity = result.get(0);
                    break;

                default:
                    throw new IllegalStateException();
            }

            objectOutput.writeObject(entity);
            objectOutput.flush();
            final byte[] bytesToWrite = byteOutput.toByteArray();
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytesToWrite.length);
            outputStream.write(bytesToWrite);
            server.log("Sent: " + entity.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
