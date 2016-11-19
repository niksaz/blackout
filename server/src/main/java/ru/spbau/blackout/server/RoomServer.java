package ru.spbau.blackout.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomServer {

    private static int PLAYERS_NUMBER = 2;

    private final int port;
    private final Deque<ClientThread> clientThreads = new ConcurrentLinkedDeque<>();
    private final AtomicInteger playersNumber = new AtomicInteger();

    public RoomServer(int port) {
        this.port = port;
    }

    public int getPlayersNumber() {
        return playersNumber.get();
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                final Socket nextSocket = serverSocket.accept();
                final ClientThread nextThread = new ClientThread(this, nextSocket);
                synchronized (System.out) {
                    System.out.println("New thread for a connection is created");
                }
                clientThreads.add(nextThread);
                playersNumber.addAndGet(1);
                nextThread.start();
                maybePlayGame(PLAYERS_NUMBER);
            }
        } catch (IOException e) {
            synchronized (System.out) {
                System.out.println("Exception caught when trying to listen on port "
                        + port + " or listening for a connection");
                System.out.println(e.getMessage());
            }
        }
    }

    private synchronized void maybePlayGame(int playersForGame) {
        if (playersNumber.get() >= playersForGame) {
            playersNumber.addAndGet(-playersForGame);
            List<ClientThread> clients = new ArrayList<>(playersForGame);
            for (int playerIndex = 0; playerIndex < playersForGame; playerIndex++) {
                clients.add(clientThreads.removeFirst());
            }
            final Thread game = new Game(clients);
            game.start();
        }
    }

    void discard(ClientThread clientThread) {
        playersNumber.decrementAndGet();
        clientThreads.remove(clientThread);

        synchronized (System.out) {
            System.out.println("Thread was disconnected.");
        }
    }

}
