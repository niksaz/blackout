package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import ru.spbau.blackout.BlackoutGame;

abstract class MenuScreen extends BlackoutScreen {

    protected static final Color  MENU_BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.1f, 1.0f);
    protected static final String MENU_BUTTON_UP_TEXTURE_PATH = "images/menuscreen/button_up.png";
    protected static final String MENU_BUTTON_DOWN_TEXTURE_PATH = "images/menuscreen/button_down.png";
    protected static final float  MENU_BUTTON_TEXT_SCALE = 1.5f;
    protected static final float  MAIN_MENU_BUTTON_PADDING = 10.0f;

    protected Stage stage;

    MenuScreen(BlackoutGame blackoutGame) {
        super(blackoutGame);

        stage = new Stage(new ExtendViewport(BlackoutGame.VIRTUAL_WORLD_WIDTH, BlackoutGame.VIRTUAL_WORLD_HEIGHT));
        Gdx.input.setInputProcessor(stage);
    }

    protected TextButton addButton(Table table, String text, Drawable upImage, Drawable downImage, EventListener listener) {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(MENU_BUTTON_TEXT_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.up = upImage;
        style.down = downImage;

        TextButton button = new TextButton(text, style);
        if (listener != null) {
            button.addListener(listener);
        }

        table.add(button).pad(MAIN_MENU_BUTTON_PADDING).row();

        return button;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(
                MenuScreen.MENU_BACKGROUND_COLOR.r, MenuScreen.MENU_BACKGROUND_COLOR.g,
                MenuScreen.MENU_BACKGROUND_COLOR.b, MenuScreen.MENU_BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }
}
