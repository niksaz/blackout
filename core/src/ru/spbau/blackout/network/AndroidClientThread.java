package ru.spbau.blackout.network;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.gamesession.TestingSessionSettings;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.screens.MenuScreen;
import ru.spbau.blackout.screens.MultiplayerTable;
import ru.spbau.blackout.screens.PlayScreenTable;
import ru.spbau.blackout.settings.GameSettings;

import static ru.spbau.blackout.BlackoutGame.HOST_NAME;
import static ru.spbau.blackout.BlackoutGame.PORT_NUMBER;

public class AndroidClientThread extends Thread {

    private static final String TAG = "AndroidClientThread";
    private static final String WAITING = "Waiting for a game.";
    private static final String READY_TO_START_MS = "Starting a game. Prepare yourself.";
    private static final String IN_PROCESS = "In process.";

    private final MultiplayerTable table;
    private GameState gameState = GameState.WAITING;
    private final AbstractServer server = new RealServer();

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
                gameState = (GameState) in.readObject();

                switch (gameState) {
                    case WAITING:
                        Gdx.app.postRunnable(() ->
                                table.getStatusLabel().setText(WAITING));
                        break;
                    case READY_TO_START:
                        Gdx.app.postRunnable(() ->
                                table.getStatusLabel().setText(READY_TO_START_MS));

                        final GameSettings settings = new GameSettings();
                        TestingSessionSettings room = (TestingSessionSettings) in.readObject();
                        room.character = (Hero.Definition) in.readObject();

                        BlackoutGame.getInstance().getScreenManager().setScreen(new GameScreen(room, server, settings));
                        break;
                    default:
                        break;
                }
            } while (gameState == GameState.WAITING && !isInterrupted());

            while (true) {
            }
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
}
