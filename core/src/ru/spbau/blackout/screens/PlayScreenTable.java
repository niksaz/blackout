package ru.spbau.blackout.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import ru.spbau.blackout.BlackoutGame;

import static ru.spbau.blackout.screens.MenuScreen.addButton;

class PlayScreenTable  {

    private static final String BACK_TEXT = "Back to main menu";
    private static final String QUICK_GAME_TEXT = "Quick Game";
    private static final String INVITE_PLAYERS_TEXT = "Invite Players";
    private static final String SHOW_INVITATIONS_TEXT = "Show Invitations";

    public static Table getTable(final BlackoutGame game, final MenuScreen screen) {
        final Table middleTable = new Table();

        final Drawable upImage = new TextureRegionDrawable(
                new TextureRegion(new Texture(MenuScreen.BUTTON_UP_TEXTURE_PATH)));
        final Drawable downImage = new TextureRegionDrawable(
                new TextureRegion(new Texture(MenuScreen.BUTTON_DOWN_TEXTURE_PATH)));

        // FIXME: just for test
        addButton(middleTable, "FIXME: test GameScreen", upImage, downImage, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float  y) {
                game.testGameScreen();
            }
        });

        addButton(middleTable, QUICK_GAME_TEXT, upImage, downImage, null);
        addButton(middleTable, INVITE_PLAYERS_TEXT, upImage, downImage, null);
        addButton(middleTable, SHOW_INVITATIONS_TEXT, upImage, downImage, null);
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
