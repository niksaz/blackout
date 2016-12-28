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

import ru.spbau.blackout.serverside.multiplayer.ClientHandler;
import ru.spbau.blackout.serverside.multiplayer.Game;

/**
 * Accepts incoming connections, creates a thread for each accepted connection (ClientHandler),
 * stored them and match them to create Games.
 */
public class RoomServer extends ServerWithLogging {

    private final int playersToStartGame;
    private final Deque<ClientHandler> clientHandlers = new ConcurrentLinkedDeque<>();
    private final AtomicInteger numberOfPlayers = new AtomicInteger();
    private int gamesCreated;

    public RoomServer(int port, int playersToStartGame, PrintStream logger, String tag) {
        super(port, logger, tag);
        this.playersToStartGame = playersToStartGame;
    }

    public void start() {
        new Thread(this::run).start();
    }

    public void discard(ClientHandler clientHandler) {
        numberOfPlayers.decrementAndGet();
        clientHandlers.remove(clientHandler);
        log("Client named " + clientHandler.getClientName() + " disconnected.");
    }

    private void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log("Server started.");
            //noinspection InfiniteLoopStatement
            do {
                final Socket nextSocket = serverSocket.accept();
                final ClientHandler clientHandler = new ClientHandler(this, nextSocket);
                final Thread clientThread = new Thread(clientHandler);
                log("New thread for a connection is created.");
                clientHandlers.add(clientHandler);
                numberOfPlayers.addAndGet(1);
                clientThread.start();
                maybePlayGame(playersToStartGame);
            } while (true);
        } catch (IOException e) {
            log("Exception caught when trying to listen on port " + port +
                    " or listening for a connection:" + e.getMessage());
        }
    }

    private synchronized void maybePlayGame(int playersForGame) {
        if (numberOfPlayers.get() >= playersForGame) {
            numberOfPlayers.addAndGet(-playersForGame);
            final List<ClientHandler> clients = new ArrayList<>(playersForGame);
            for (int playerIndex = 0; playerIndex < playersForGame; playerIndex++) {
                clients.add(clientHandlers.removeFirst());
            }
            final Thread newGameThread = new Game(this, clients, gamesCreated++);
            newGameThread.start();
        }
    }
}
