package ru.spbau.blackout.server;

public class Main {
    private static final int PORT = 54321;

    public static void main(String[] args) {
        new RoomServer(PORT).run();
    }
}
