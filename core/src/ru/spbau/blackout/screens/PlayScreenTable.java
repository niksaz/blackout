package ru.spbau.blackout.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import ru.spbau.blackout.BlackoutGame;

import static ru.spbau.blackout.screens.MenuScreen.addButton;

public class PlayScreenTable  {

    private static final String SINGLE_PLAYER_GAME_TEXT = "Single player game";
    private static final String MULTIPLAYER_GAME_TEXT = "Multiplayer game";
    private static final String BACK_TEXT = "Back";

    public static Table getTable(final MenuScreen screen) {
        final Table middleTable = new Table();

        final Drawable upImage = new TextureRegionDrawable(
                new TextureRegion(new Texture(MenuScreen.BUTTON_UP_TEXTURE_PATH)));
        final Drawable downImage = new TextureRegionDrawable(
                new TextureRegion(new Texture(MenuScreen.BUTTON_DOWN_TEXTURE_PATH)));

        addButton(middleTable, SINGLE_PLAYER_GAME_TEXT, upImage, downImage, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float  y) {
                BlackoutGame.get().testGameScreen();
            }
        });

        addButton(middleTable, MULTIPLAYER_GAME_TEXT, upImage, downImage, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.changeMiddleTable(MultiplayerTable.getTable(screen));
            }
        });
        addButton(middleTable, BACK_TEXT, upImage, downImage, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.changeMiddleTable(MainMenuTable.getTable(screen));
            }
        });

        middleTable.setFillParent(true);
        return middleTable;
    }

}
