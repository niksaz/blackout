package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.play.services.PlayServicesInCore;
import ru.spbau.blackout.utils.AssetLoader;

import static ru.spbau.blackout.BlackoutGame.HOST_NAME;
import static ru.spbau.blackout.BlackoutGame.PORT_NUMBER;
import static ru.spbau.blackout.screens.MenuScreen.addButton;

class MultiplayerTable {

    private static final String TAG = "MultiplayerTable";
    private static final String BACK_TEXT = "Back";

    private static final int POLLING_TIME_MS = 250;

    private final Table middleTable;
    private final Label status;
    private final BlackoutGame game;
    private final MenuScreen screen;
    private final AtomicBoolean shouldClose;

    private MultiplayerTable(BlackoutGame game, MenuScreen screen) {
        this.game = game;
        this.screen = screen;

        middleTable = new Table();
        final Label.LabelStyle style = new Label.LabelStyle();
        style.font = AssetLoader.getInstance().getFont();
        status = new Label("Connecting to the server...", style);
        middleTable.add(status).row();
        shouldClose = new AtomicBoolean(false);
    }

    static Table getTable(final BlackoutGame game, final MenuScreen screen) {
        final MultiplayerTable result = new MultiplayerTable(game, screen);

        result.runUpdateThread();

        final Drawable upImage = new TextureRegionDrawable(
                new TextureRegion(new Texture(MenuScreen.BUTTON_UP_TEXTURE_PATH)));
        final Drawable downImage = new TextureRegionDrawable(
                new TextureRegion(new Texture(MenuScreen.BUTTON_DOWN_TEXTURE_PATH)));

        addButton(result.middleTable, BACK_TEXT, upImage, downImage, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                result.shouldClose.set(true);
            }
        });

        result.middleTable.setFillParent(true);
        return result.middleTable;
    }

    private void runUpdateThread() {
        new Thread(() -> {
            try (
                    Socket echoSocket = new Socket(HOST_NAME, PORT_NUMBER);
                    PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()))
            ) {
                Gdx.app.log(TAG, "Started");
                out.println(BlackoutGame.getInstance().getPlayServicesInCore().getPlayServices().getPlayerName());

                String inputLine;
                while (!shouldClose.get() && (inputLine = in.readLine()) != null) {
                    try {
                        final String readLine = inputLine;
                        Gdx.app.postRunnable(() ->
                                status.setText(playersSentence(Integer.parseInt(readLine))));
                        Thread.sleep(POLLING_TIME_MS);
                    } catch (NumberFormatException | InterruptedException ignored) {
                    }
                    out.println("");
                }
            } catch (UnknownHostException e) {
                Gdx.app.log(TAG, "Don't know about host " + HOST_NAME);
            } catch (IOException e) {
                Gdx.app.log(TAG, "Couldn't get I/O for the connection to " + HOST_NAME);
            }
            finally {
                Gdx.app.postRunnable(() -> screen.changeMiddleTable(PlayScreenTable.getTable(game, screen)));
            }
            Gdx.app.log(TAG, "Stopped");
        }).start();
    }

    private String playersSentence(int number) {
        if (number == 1) {
            return number + " player is waiting";
        } else {
            return number + " players are waiting";
        }
    }

}
