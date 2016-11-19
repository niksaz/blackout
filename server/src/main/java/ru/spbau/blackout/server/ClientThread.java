package ru.spbau.blackout.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.spbau.blackout.screens.MultiplayerTable;

class ClientThread extends Thread {

    private static final int READ_TIMEOUT_MS = 5000;

    private final RoomServer server;
    private final Socket socket;
    private final AtomicBoolean gameStarted = new AtomicBoolean();

    ClientThread(RoomServer server, Socket socket) {
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
                if (gameStarted.get()) {
                    out.println(MultiplayerTable.GAME_IS_STARTED);
                    break;
                } else {
                    //noinspection SynchronizationOnLocalVariableOrMethodParameter
                    out.println(server.getPlayersNumber());

                }
            } while (in.readLine() != null);
        } catch (IOException ignored) {
        } finally {
            if (!gameStarted.get()) {
                server.discard(this);
            }
        }
    }

    public void startGame(Game game) {
        gameStarted.set(true);
    }

}
