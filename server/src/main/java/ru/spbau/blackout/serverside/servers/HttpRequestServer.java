package ru.spbau.blackout.serverside.servers;

import com.sun.net.httpserver.HttpServer;

import java.io.PrintStream;
import java.net.InetSocketAddress;

import ru.spbau.blackout.database.Database;

/**
 * Server for responding of loading and updatePhysics of player's information. Stores connection to MongoDB database.
 */
public class HttpRequestServer extends ServerWithLogging {

    public HttpRequestServer(int port, PrintStream logger, String tag) {
        super(port, logger, tag);
    }

    public void start() {
        try {
            final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext(Database.LOAD_COMMAND, new LoadRequestHandler(this));
            server.createContext(Database.UPGRADE_COMMAND, new UpgradeRequestHandler(this));
            server.createContext(Database.SETTINGS_SYNCHRONIZE_COMMAND, new SettingsSynchronizeHandler(this));
            server.setExecutor(null);
            server.start();
            log("Server started.");
        } catch (Exception e) {
            log("Exception while creating HttpServer on port " + port + ": " + e.getMessage());
        }
    }
}
