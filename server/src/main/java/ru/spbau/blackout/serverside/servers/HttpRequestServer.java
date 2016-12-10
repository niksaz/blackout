package ru.spbau.blackout.serverside.servers;

import com.sun.net.httpserver.HttpServer;

import java.io.PrintStream;
import java.net.InetSocketAddress;

public class HttpRequestServer extends ServerWithLogging {

    public HttpRequestServer(int port, PrintStream logger, String tag) {
        super(port, logger, tag);
    }

    public void start() {
        try {
            final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/login", new LoginRequestHandler());
            server.setExecutor(null);
            server.start();
        } catch (Exception e) {
            log("Exception while creating HttpServer on port " + port + ":" + e.getMessage());
        }
    }
}
