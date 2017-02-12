package ru.spbau.blackout.serverside.servers.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import ru.spbau.blackout.serverside.database.DatabaseAccessor;
import ru.spbau.blackout.serverside.servers.HttpRequestServer;

/**
 * Handler for updatePhysics queries from clients.
 * Assumes that client have logged in before so his entry exists in the database.
 */
public class UpgradeRequestHandler implements HttpHandler {

    private final HttpRequestServer server;

    public UpgradeRequestHandler(HttpRequestServer server) {
        this.server = server;
    }

    @Override
    public void handle(HttpExchange exchange) {
        try (InputStream input = exchange.getRequestBody();
             DataInputStream inputStream = new DataInputStream(input)
        ) {
            boolean successful = DatabaseAccessor.getInstance().handleUpdateFromInputStream(inputStream);
            if (successful) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                server.log("Successful upgrade request handle");
            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_FORBIDDEN, 0);
                server.log("Unsuccessful upgrade request handle");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
