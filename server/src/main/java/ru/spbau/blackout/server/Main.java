package ru.spbau.blackout.server;

/**
 * Class for starting servers.
 */
public class Main {

    private static final int PORT = 48800;

    public static void main(String[] args) {
        new RoomServer(PORT).run();
    }
}
