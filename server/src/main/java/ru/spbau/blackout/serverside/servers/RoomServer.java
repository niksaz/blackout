package ru.spbau.blackout.serverside.servers;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

import ru.spbau.blackout.serverside.multiplayer.Game;
import ru.spbau.blackout.serverside.multiplayer.RoomClientThread;

/**
 * Accepts incoming connections, creates a thread for each accepted connection (ClientThread),
 * stored them and match them to create Games.
 */
public class RoomServer extends ServerWithLogging {

    private static final int PLAYERS_NUMBER_TO_START_GAME = 1;

    private final Deque<RoomClientThread> clientThreads = new ConcurrentLinkedDeque<>();
    private final AtomicInteger playersNumber = new AtomicInteger();
    private int gamesCreated;

    public RoomServer(int port, PrintStream logger, String tag) {
        super(port, logger, tag);
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            do {
                final Socket nextSocket = serverSocket.accept();
                final RoomClientThread nextThread = new RoomClientThread(this, nextSocket);
                log("New thread for a connection is created.");
                clientThreads.add(nextThread);
                playersNumber.addAndGet(1);
                nextThread.start();
                maybePlayGame(PLAYERS_NUMBER_TO_START_GAME);
            } while (true);
        } catch (IOException e) {
            log("Exception caught when trying to listen on port " + port +
                    " or listening for a connection:" + e.getMessage());
        }
    }

    public void discard(RoomClientThread clientThread) {
        playersNumber.decrementAndGet();
        clientThreads.remove(clientThread);
        log("Client named " + clientThread.getClientName() + " disconnected.");
    }

    private synchronized void maybePlayGame(int playersForGame) {
        if (playersNumber.get() >= playersForGame) {
            playersNumber.addAndGet(-playersForGame);
            final List<RoomClientThread> clients = new ArrayList<>(playersForGame);
            for (int playerIndex = 0; playerIndex < playersForGame; playerIndex++) {
                clients.add(clientThreads.removeFirst());
            }
            final Thread newGameThread = new Game(this, clients, gamesCreated++);
            newGameThread.start();
        }
    }
}
