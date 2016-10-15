package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.play.services.PlayServicesInCore;

import static ru.spbau.blackout.BlackoutGame.hostName;
import static ru.spbau.blackout.BlackoutGame.portNumber;
import static ru.spbau.blackout.screens.MenuScreen.addButton;

class PlayScreenTable  {

    private static final String TAG = "PlayScreenTable";

    private static final String SINGLE_PLAYER_GAME_TEXT = "Single player game";
    private static final String MULTIPLAYER_GAME_TEXT = "Multiplayer game";
    private static final String BACK_TEXT = "Back to main menu";

    static Table getTable(final BlackoutGame game, final MenuScreen screen) {
        final Table middleTable = new Table();

        final Drawable upImage = new TextureRegionDrawable(
                new TextureRegion(new Texture(MenuScreen.BUTTON_UP_TEXTURE_PATH)));
        final Drawable downImage = new TextureRegionDrawable(
                new TextureRegion(new Texture(MenuScreen.BUTTON_DOWN_TEXTURE_PATH)));

        addButton(middleTable, SINGLE_PLAYER_GAME_TEXT, upImage, downImage, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float  y) {
                game.testGameScreen();
            }
        });

        addButton(middleTable, MULTIPLAYER_GAME_TEXT, upImage, downImage, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                new Thread(() -> {
                    try (
                        Socket echoSocket = new Socket(hostName, portNumber);
                        PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()))
                    ) {
                        Gdx.app.log(TAG, "Started");
                        out.println(PlayServicesInCore.getInstance().getPlayServices().getPlayerName());
                        final String response = in.readLine();
                    } catch (UnknownHostException e) {
                        Gdx.app.log(TAG, "Don't know about host " + hostName);
                    } catch (IOException e) {
                        Gdx.app.log(TAG, "Couldn't get I/O for the connection to " + hostName);
                    }
                    Gdx.app.log(TAG, "Stopped");
                }).start();
            }
        });
        addButton(middleTable, BACK_TEXT, upImage, downImage, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.changeMiddleTable(MainMenuTable.getTable(game, screen));
            }
        });

        middleTable.setFillParent(true);
        return middleTable;
    }

}
