package ru.spbau.blackout.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Accepts incoming connections, creates a thread for each accepted connection (ClientThread),
 * stored them and match them to create Games.
 */
class RoomServer {

    private static final int PLAYERS_NUMBER = 2;

    private final int port;
    private final Deque<ClientThread> clientThreads = new ConcurrentLinkedDeque<>();
    private final AtomicInteger playersNumber = new AtomicInteger();
    private int gamesCreated;

    RoomServer(int port) {
        this.port = port;
    }

    void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            do {
                final Socket nextSocket = serverSocket.accept();
                final ClientThread nextThread = new ClientThread(this, nextSocket);
                log("New thread for a connection is created.");
                clientThreads.add(nextThread);
                playersNumber.addAndGet(1);
                nextThread.start();
                maybePlayGame(PLAYERS_NUMBER);
            } while (true);
        } catch (IOException e) {
            log("Exception caught when trying to listen on port " + port +
                " or listening for a connection:" + e.getMessage());
        }
    }

    void discard(ClientThread clientThread) {
        playersNumber.decrementAndGet();
        clientThreads.remove(clientThread);
        log("Client named " + clientThread.getClientName() + " disconnected.");
    }

    void log(String message) {
        synchronized (System.out) {
            System.out.println(message);
        }
    }

    private synchronized void maybePlayGame(int playersForGame) {
        if (playersNumber.get() >= playersForGame) {
            playersNumber.addAndGet(-playersForGame);
            final List<ClientThread> clients = new ArrayList<>(playersForGame);
            for (int playerIndex = 0; playerIndex < playersForGame; playerIndex++) {
                clients.add(clientThreads.removeFirst());
            }
            final Thread newGameThread = new Game(this, clients, gamesCreated++);
            newGameThread.start();
        }
    }
}
