package ru.spbau.blackout.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class RoomServer {

    private final int port;
    private final Deque<RoomClientThread> roomClientThreads = new ConcurrentLinkedDeque<>();
    private final AtomicInteger playersNumber = new AtomicInteger();

    public AtomicInteger getPlayersNumber() {
        return playersNumber;
    }

    public RoomServer(int port) {
        this.port = port;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                final Socket nextSocket = serverSocket.accept();
                final RoomClientThread nextThread = new RoomClientThread(this, nextSocket);
                synchronized (System.out) {
                    System.out.println("New thread for a connection is created");
                }
                roomClientThreads.add(nextThread);
                playersNumber.addAndGet(1);
                nextThread.start();
            }
        } catch (IOException e) {
            synchronized (System.out) {
                System.out.println("Exception caught when trying to listen on port "
                        + port + " or listening for a connection");
                System.out.println(e.getMessage());
            }
        }
    }

    void discard(RoomClientThread clientThread) {
        roomClientThreads.remove(clientThread);
        playersNumber.addAndGet(-1);
        synchronized (System.out) {
            System.out.println("Thread is discarded");
        }
    }

}
