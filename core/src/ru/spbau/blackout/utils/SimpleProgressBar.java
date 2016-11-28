package ru.spbau.blackout.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


// I know that libgdx has its own API for progress bars, but this one is much better in some cases.
// Also it's adapted for the project (project-specific methods like load and doneLoading).
/**
 * Provides easy and powerful API for creating progress bars.
 */
public class SimpleProgressBar extends Actor {
    private String fullTexturePath;
    private String emptyTexturePath;

    private TextureRegionDrawable full;
    private TextureRegionDrawable empty;

    private /*final*/ int fullWidth;
    private float minValue;
    private float maxValue;
    private float normalizedValue;


    public SimpleProgressBar(String emptyTexturePath, String fullTexturePath, float minValue, float maxValue) {
        this.fullTexturePath = fullTexturePath;
        this.emptyTexturePath = emptyTexturePath;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.normalizedValue = minValue;
    }

    public SimpleProgressBar(String emptyTexturePath, String fullTexturePath) {
        this(emptyTexturePath, fullTexturePath, 0, 1);
    }


    public void load(AssetManager assets) {
        assets.load(this.fullTexturePath, Texture.class);
        assets.load(this.emptyTexturePath, Texture.class);
    }

    public void doneLoading(AssetManager assets) {
        Texture fullTexture = assets.get(this.fullTexturePath, Texture.class);
        this.fullWidth = fullTexture.getWidth();
        this.full = new TextureRegionDrawable(new TextureRegion(fullTexture));
        this.empty = new TextureRegionDrawable(new TextureRegion(assets.get(this.emptyTexturePath, Texture.class)));
        this.setNormalizedValue(minValue);

        this.fullTexturePath = null;
        this.emptyTexturePath = null;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        this.full.getRegion().setRegionWidth((int) Math.floor(normalizedValue * this.fullWidth));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        empty.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        full.draw(batch, this.getX(), this.getY(), normalizedValue * this.getWidth(), this.getHeight());
    }

    public void setNormalizedValue(float normalizedValue) {
        this.normalizedValue = (normalizedValue - this.minValue) / this.maxValue;  // from 0 to 1
    }
}
