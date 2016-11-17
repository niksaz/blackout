package ru.spbau.blackout.server;

import java.util.List;

class Game extends Thread {

    private final List<RoomClientThread> clients;

    Game(List<RoomClientThread> clients) {
        this.clients = clients;
    }

    public void run() {
        synchronized (System.out) {
            System.out.println("New game has just started!");
        }
        for (RoomClientThread thread : clients) {
            thread.startGame(this);
        }
    }
}
