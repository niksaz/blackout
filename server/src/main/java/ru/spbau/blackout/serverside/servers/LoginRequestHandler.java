package ru.spbau.blackout.serverside.servers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class LoginRequestHandler implements HttpHandler {

    private final HttpRequestServer server;

    public LoginRequestHandler(HttpRequestServer server) {
        this.server = server;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (
             InputStream input = exchange.getRequestBody();
             DataInputStream inputStream = new DataInputStream(input);
             OutputStream outputStream = exchange.getResponseBody()
        ) {
            final String name = inputStream.readUTF();
            final String response = "Hello, " + name + "!";
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes().length);
            outputStream.write(response.getBytes());
            server.log(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
