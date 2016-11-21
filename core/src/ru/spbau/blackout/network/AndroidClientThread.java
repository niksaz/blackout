package ru.spbau.blackout.network;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.rooms.TestingSessionSettings;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.screens.MenuScreen;
import ru.spbau.blackout.screens.MultiplayerTable;
import ru.spbau.blackout.screens.PlayScreenTable;
import ru.spbau.blackout.settings.GameSettings;

import static ru.spbau.blackout.BlackoutGame.HOST_NAME;
import static ru.spbau.blackout.BlackoutGame.PORT_NUMBER;

public class AndroidClientThread extends Thread {

    private static final String TAG = "AndroidClientThread";
    private static final String READY_TO_START_MS = "Starting a game. Prepare yourself.";

    private final MultiplayerTable table;
    private GameState gameState = GameState.WAITING;

    public AndroidClientThread(MultiplayerTable table) {
        this.table = table;
    }

    @Override
    public void run() {
        Gdx.app.log(TAG, "Started");
        try (
            Socket socket = new Socket(HOST_NAME, PORT_NUMBER);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            socket.setSoTimeout(Network.SOCKET_IO_TIMEOUT_MS);
            out.writeUTF(BlackoutGame.getInstance().getPlayServicesInCore().getPlayServices().getPlayerName());
            out.flush();

            do {
                final GameState serverGameState = (GameState) in.readObject();
                int numberOfPlayers = 0;
                if (serverGameState == GameState.WAITING) {
                    numberOfPlayers = in.readInt();
                }

                switch (gameState) {
                    case WAITING:
                        final int copyForLambda = numberOfPlayers;
                        Gdx.app.postRunnable(() ->
                                table.getStatusLabel().setText(playersSentence(copyForLambda)));
                        break;
                    case READY_TO_START:
                        // get test

                        GameSettings settings = new GameSettings();
                        TestingSessionSettings room = null;
                        BlackoutGame.getInstance().getScreenManager().setScreen(new GameScreen(room, settings));

                        Gdx.app.postRunnable(() ->
                                table.getStatusLabel().setText(READY_TO_START_MS));
                        break;
                    case IN_PROCESS:
                        Gdx.app.postRunnable(() ->
                                table.getStatusLabel().setText("In process."));
                    default:
                        break;
                }

                gameState = serverGameState;
            } while (gameState != GameState.FINISHED && !isInterrupted());

        } catch (UnknownHostException e) {
            Gdx.app.log(TAG, "Don't know about host " + HOST_NAME);
        }
        catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        finally {
            Gdx.app.postRunnable(() -> {
                final MenuScreen menuScreen = table.getMenuScreen();
                menuScreen.changeMiddleTable(PlayScreenTable.getTable(menuScreen));
            });
        }
        Gdx.app.log(TAG, "Stopped");
    }

    private static String playersSentence(int number) {
        if (number == 1) {
            return number + " player is waiting";
        } else {
            return number + " players are waiting";
        }
    }
}
