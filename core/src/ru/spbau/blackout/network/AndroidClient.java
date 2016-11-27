package ru.spbau.blackout.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.GameWorld;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.gamesession.TestingSessionSettings;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.screens.MenuScreen;
import ru.spbau.blackout.screens.MultiplayerTable;
import ru.spbau.blackout.screens.PlayScreenTable;
import ru.spbau.blackout.settings.GameSettings;

import static java.lang.Thread.sleep;
import static ru.spbau.blackout.BlackoutGame.HOST_NAME;
import static ru.spbau.blackout.BlackoutGame.PORT_NUMBER;

public class AndroidClient implements Runnable, AbstractServer {

    private static final String TAG = "AndroidClient";
    private static final String WAITING = "Waiting for a game.";
    private static final String READY_TO_START_MS = "Starting a game. Prepare yourself.";

    private final MultiplayerTable table;
    private volatile boolean isInterrupted = false;
    private volatile Vector2 velocityToSend = new Vector2();
    private GameScreen gameScreen;

    public AndroidClient(MultiplayerTable table) {
        this.table = table;
    }

    @Override
    public void run() {
        try (
            Socket socket = new Socket(HOST_NAME, PORT_NUMBER);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            socket.setSoTimeout(Network.SOCKET_IO_TIMEOUT_MS);
            out.writeUTF(BlackoutGame.getInstance().getPlayServicesInCore().getPlayServices().getPlayerName());
            out.flush();

            GameState gameState;
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

                        // using the fact that AndroidClient is AbstractServer itself.
                        // so synchronizing on server on loading
                        synchronized (this) {
                            gameScreen = new GameScreen(room, this, settings);
                            BlackoutGame.getInstance().getScreenManager().setScreen(gameScreen);
                            try {
                                wait();
                            } catch (InterruptedException ignored) {
                                isInterrupted = true;
                            }
                        }
                        out.writeBoolean(!isInterrupted);
                        out.flush();
                        break;
                    default:
                        break;
                }
            } while (gameState == GameState.WAITING && !isInterrupted);

            final Thread outputToServerThread = new Thread(() -> {
                while (!isInterrupted) {
                    try {
                        Vector2 sending = new Vector2(velocityToSend);
                        out.writeObject(sending);
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        isInterrupted = true;
                    }
                    // sleeping to not send position too often.
                    try {
                        sleep(Network.SLEEPING_TIME_TO_ACHIEVE_FRAME_RATE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        isInterrupted = true;
                    }
                }
            });
            outputToServerThread.start();

            // waiting for the first world to arrive
            socket.setSoTimeout(0);
            while (!isInterrupted) {
                // should read game world here from inputStream
                final GameWorld currentWorld = gameScreen.getGameWorld();

                //noinspection SynchronizationOnLocalVariableOrMethodParameter
                synchronized (currentWorld) {
                    currentWorld.inplaceDeserialize(in);
                }

                // should get worlds regularly after the first one
                socket.setSoTimeout(Network.SOCKET_IO_TIMEOUT_MS);
            }
        } catch (UnknownHostException e) {
            Gdx.app.log(TAG, "Don't know about host " + HOST_NAME);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } finally {
            isInterrupted = true;
            Gdx.app.postRunnable(() -> {
                final MenuScreen menuScreen = table.getMenuScreen();
                menuScreen.changeMiddleTable(PlayScreenTable.getTable(menuScreen));
                BlackoutGame.getInstance().getScreenManager().setScreen(menuScreen);
            });
        }
    }

    public void interrupt() {
        isInterrupted = true;
    }

    @Override
    public void sendSelfVelocity(Vector2 velocity) {
        velocityToSend = velocity;
    }
}
