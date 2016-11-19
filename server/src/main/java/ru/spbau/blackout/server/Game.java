package ru.spbau.blackout.server;

import java.util.List;

class Game extends Thread {

    private final List<ClientThread> clients;

    Game(List<ClientThread> clients) {
        this.clients = clients;
    }

    public void run() {
        synchronized (System.out) {
            System.out.println("New game has just started!");
        }
        for (ClientThread thread : clients) {
            thread.startGame(this);
        }

    }
}
