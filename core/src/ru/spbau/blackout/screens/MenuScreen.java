package ru.spbau.blackout.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import ru.spbau.blackout.BlackoutGame;

abstract class MenuScreen extends BlackoutScreen {

    static final Color  MENU_BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.1f, 1.0f);
    static final String MENU_BUTTON_UP_TEXTURE_PATH = "images/menuscreen/button_up.png";
    static final String MENU_BUTTON_DOWN_TEXTURE_PATH = "images/menuscreen/button_down.png";
    static final float  MENU_BUTTON_TEXT_SCALE = 1.5f;
    static final float  MAIN_MENU_BUTTON_PADDING = 10.0f;

    MenuScreen(BlackoutGame blackoutGame) {
        super(blackoutGame);
    }

    TextButton addButton(Table table, String text, Drawable upImage, Drawable downImage, EventListener listener) {
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

}
