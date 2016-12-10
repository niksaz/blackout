package ru.spbau.blackout.serverside.servers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class LoginRequestHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final String response = "This is the response";
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length());
        final OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }
}
