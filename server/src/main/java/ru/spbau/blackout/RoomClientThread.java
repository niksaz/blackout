package ru.spbau.blackout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class RoomClientThread extends Thread {

    private final Server server;
    private final Socket socket;

    RoomClientThread(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    public void run() {
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String inputLine = in.readLine();
            synchronized (System.out) {
                System.out.println(inputLine + " connected");
            }
            while ((inputLine = in.readLine()) != null) {
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            server.discard(this);
        }
    }

}
