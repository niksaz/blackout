package ru.spbau.blackout;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private static final int PORT = 54321;

    private final ArrayList<RoomClientThread> roomClientThreads;
    private final int port;

    public static void main(String[] args) throws IOException {
        new Server(PORT).run();
    }

    private Server(int port) {
        roomClientThreads = new ArrayList<>();
        this.port = port;
    }

    private void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                final Socket nextSocket = serverSocket.accept();
                final RoomClientThread nextThread = new RoomClientThread(this, nextSocket);
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

    void discard(RoomClientThread clientThread) {
        synchronized (roomClientThreads) {
            roomClientThreads.remove(clientThread);
        }
        synchronized (System.out) {
            System.out.println("Thread is discarded");
        }
    }

}
