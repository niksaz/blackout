package ru.spbau.blackout.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class AssetLoader {

    private static final String FONT_PATH = "fonts/good_dog_plain.ttf";

    private static final int FONT_BLACKOUT_LABEL_SIZE = 80;
    private static final float FONT_BLACKOUT_LABEL_WIDTH = 2f;

    private static final int FONT_SIZE = 40;
    private static final float FONT_WIDTH = 0f;

    private static AssetLoader instance;

    private BitmapFont fontBlackoutLabel;
    private BitmapFont font;

    private AssetLoader() {
    }

    public static AssetLoader getInstance() {
        if (instance == null) {
            instance = new AssetLoader();
        }
        return instance;
    }

    public void loadFonts() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_PATH));
        fontBlackoutLabel = generator.generateFont(
                getParameter(FONT_BLACKOUT_LABEL_SIZE, FONT_BLACKOUT_LABEL_WIDTH));
        font = generator.generateFont(getParameter(FONT_SIZE, FONT_WIDTH));
        generator.dispose();
    }


    public BitmapFont getFontBlackoutLabel() {
        return fontBlackoutLabel;
    }

    public BitmapFont getFont() {
        return font;
    }

    private FreeTypeFontParameter getParameter(int font_size, float width) {
        final FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = font_size;
        parameter.borderWidth = width;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.minFilter = Texture.TextureFilter.Linear;
        return parameter;
    }

}
