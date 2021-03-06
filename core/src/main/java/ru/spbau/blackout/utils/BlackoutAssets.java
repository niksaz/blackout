package ru.spbau.blackout.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Loads fonts, skins, colors which are later used for menu ui. Loading is done right after a start of the app.
 */
public class BlackoutAssets {

    private static final String FONT_PATH = "fonts/good_dog_plain.ttf";
    private static final String DEFAULT_SKIN_JSON_PATH = "ui/uiskin.json";
    private static final String DEFAULT_SKIN_ATLAS_PATH = "ui/uiskin.atlas";

    private static final int FONT_BLACKOUT_LABEL_SIZE = 80;
    private static final float FONT_BLACKOUT_LABEL_WIDTH = 2f;

    private static final int FONT_SIZE = 40;
    private static final float FONT_WIDTH = 0f;

    private BitmapFont blackoutLabelFont;
    private BitmapFont font;
    private Skin defaultSkin;
    private Color backgroundColor;

    public void load() {
        loadFonts();
        loadSkins();
        loadColors();
    }

    public BitmapFont getBlackoutLabelFont() {
        return blackoutLabelFont;
    }

    public BitmapFont getFont() {
        return font;
    }

    public Skin getDefaultSkin() {
        return defaultSkin;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    private void loadFonts() {
        final FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT_PATH));
        blackoutLabelFont = generator.generateFont(
                getParameter(FONT_BLACKOUT_LABEL_SIZE, FONT_BLACKOUT_LABEL_WIDTH));
        font = generator.generateFont(getParameter(FONT_SIZE, FONT_WIDTH));
        generator.dispose();
    }

    private void loadSkins() {
        final TextureAtlas defaultSkinAtlas = new TextureAtlas(DEFAULT_SKIN_ATLAS_PATH);
        defaultSkin = new Skin();
        defaultSkin.addRegions(defaultSkinAtlas);
        defaultSkin.add("default-font", getFont());
        defaultSkin.add("blackout-font", getBlackoutLabelFont());
        defaultSkin.load(Gdx.files.internal(DEFAULT_SKIN_JSON_PATH));
    }

    private void loadColors() {
        backgroundColor = getDefaultSkin().getColor("gray");
    }

    private FreeTypeFontParameter getParameter(int font_size, float width) {
        final FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = font_size;
        parameter.borderWidth = width;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        return parameter;
    }
}
