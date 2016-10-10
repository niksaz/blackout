package ru.spbau.blackout.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import ru.spbau.blackout.BlackoutGame;

class PlayScreen extends MenuScreen {

    private static final String PLAY_SCREEN_BACK_TEXT = "Back to main menu";
    private static final String PLAY_SCREEN_QUICK_GAME_TEXT = "Quick Game";
    private static final String PLAY_SCREEN_INVITE_PLAYERS_TEXT = "Invite Players";
    private static final String PLAY_SCREEN_SHOW_INVITATIONS_TEXT = "Show Invitations";

    PlayScreen(BlackoutGame game) {
        super(game);

        final Table middleTable = new Table();

        final Drawable upImage = new TextureRegionDrawable(
                new TextureRegion(new Texture(MENU_BUTTON_UP_TEXTURE_PATH)));
        final Drawable downImage = new TextureRegionDrawable(
                new TextureRegion(new Texture(MENU_BUTTON_DOWN_TEXTURE_PATH)));

        // FIXME: just for test
        addButton(middleTable, "FIXME: test GameScreen", upImage, downImage, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float  y) {
                PlayScreen.this.game.testGameScreen();
            }
        });


        addButton(middleTable, PLAY_SCREEN_QUICK_GAME_TEXT, upImage, downImage, null);
        addButton(middleTable, PLAY_SCREEN_INVITE_PLAYERS_TEXT, upImage, downImage, null);
        addButton(middleTable, PLAY_SCREEN_SHOW_INVITATIONS_TEXT, upImage, downImage, null);
        addButton(middleTable, PLAY_SCREEN_BACK_TEXT, upImage, downImage, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // FIXME: ScreenManager
                PlayScreen.this.game.setScreen(new MainMenu(PlayScreen.this.game));
            }
        });

        middleTable.setFillParent(true);
        stage.addActor(middleTable);
    }

}
