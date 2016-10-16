package ru.spbau.blackout.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class RoomServer {

    private final ArrayList<ru.spbau.blackout.server.RoomClientThread> roomClientThreads = new ArrayList<>();
    private final int port;

    public RoomServer(int port) {
        this.port = port;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                final Socket nextSocket = serverSocket.accept();
                final ru.spbau.blackout.server.RoomClientThread nextThread = new ru.spbau.blackout.server.RoomClientThread(this, nextSocket);
                synchronized (System.out) {
                    System.out.println("New thread for a connection is created");
                }
                synchronized (roomClientThreads) {
                    roomClientThreads.add(nextThread);
                }
                nextThread.start();
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + port + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    void discard(ru.spbau.blackout.server.RoomClientThread clientThread) {
        synchronized (roomClientThreads) {
            roomClientThreads.remove(clientThread);
        }
        synchronized (System.out) {
            System.out.println("Thread is discarded");
        }
    }

    ArrayList<ru.spbau.blackout.server.RoomClientThread> getRoomClients() {
        return roomClientThreads;
    }

}
