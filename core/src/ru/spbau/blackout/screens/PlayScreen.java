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

    PlayScreen(BlackoutGame game) {
        super(game);

        Table middleTable = new Table();

        final Drawable upImage =
                new TextureRegionDrawable(new TextureRegion(new Texture(MENU_BUTTON_UP_TEXTURE_PATH)));
        final Drawable downImage =
                new TextureRegionDrawable(new TextureRegion(new Texture(MENU_BUTTON_DOWN_TEXTURE_PATH)));

        addButton(middleTable, PLAY_SCREEN_BACK_TEXT, upImage, downImage, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                blackoutGame.setScreen(new MainMenu(blackoutGame));
            }
        });

        middleTable.setFillParent(true);
        stage.addActor(middleTable);
    }

}
