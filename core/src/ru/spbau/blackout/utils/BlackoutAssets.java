package ru.spbau.blackout.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class BlackoutAssets {

    private static final String FONT_PATH = "fonts/good_dog_plain.ttf";
    private static final String DEFAULT_SKIN_JSON_PATH = "ui/uiskin.json";
    private static final String DEFAULT_SKIN_ATLAS_PATH = "ui/uiskin.atlas";

    private static final int FONT_BLACKOUT_LABEL_SIZE = 80;
    private static final float FONT_BLACKOUT_LABEL_WIDTH = 2f;

    private static final int FONT_SIZE = 40;
    private static final float FONT_WIDTH = 0f;

    private BitmapFont fontBlackoutLabel;
    private BitmapFont font;
    private Skin defaultSkin;

    public void load() {
        loadFonts();
        loadSkins();
    }

    public BitmapFont getFontBlackoutLabel() {
        return fontBlackoutLabel;
    }

    public BitmapFont getFont() {
        return font;
    }

    public Skin getDefaultSkin() {
        return defaultSkin;
    }

    private void loadFonts() {
        final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_PATH));
        fontBlackoutLabel = generator.generateFont(
                getParameter(FONT_BLACKOUT_LABEL_SIZE, FONT_BLACKOUT_LABEL_WIDTH));
        font = generator.generateFont(getParameter(FONT_SIZE, FONT_WIDTH));
        generator.dispose();
    }

    private void loadSkins() {
        final TextureAtlas defaultSkinAtlas = new TextureAtlas(DEFAULT_SKIN_ATLAS_PATH);
        defaultSkin = new Skin();
        defaultSkin.addRegions(defaultSkinAtlas);
        defaultSkin.add("default-font", getFont());
        defaultSkin.load(Gdx.files.internal(DEFAULT_SKIN_JSON_PATH));
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
