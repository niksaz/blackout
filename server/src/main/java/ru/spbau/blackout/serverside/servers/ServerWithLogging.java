package ru.spbau.blackout.serverside.servers;

import java.io.PrintStream;

/**
 * Server base with intended port and ability to log.
 */
abstract class ServerWithLogging {

    final int port;
    private final PrintStream logger;
    private final String tag;

    ServerWithLogging(int port, PrintStream logger, String tag) {
        this.port = port;
        this.logger = logger;
        this.tag = tag;
    }

    public void log(String logMessage) {
        synchronized (logger) {
            logger.println("[" + tag + "] " + logMessage);
        }
    }
}
