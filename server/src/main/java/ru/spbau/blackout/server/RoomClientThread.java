package ru.spbau.blackout.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class RoomClientThread extends Thread {

    private static final int READ_TIMEOUT_MS = 5000;

    private final RoomServer server;
    private final Socket socket;

    RoomClientThread(RoomServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    public void run() {
        try (
            Socket socket = this.socket;
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            socket.setSoTimeout(READ_TIMEOUT_MS);
            String inputLine = in.readLine();
            synchronized (System.out) {
                System.out.println(inputLine + " connected");
            }
            do {
                //noinspection SynchronizationOnLocalVariableOrMethodParameter
                out.println(server.getPlayersNumber().get());
            } while (in.readLine() != null);
        } catch (IOException ignored) {
        } finally {
            server.discard(this);
        }
    }

}
