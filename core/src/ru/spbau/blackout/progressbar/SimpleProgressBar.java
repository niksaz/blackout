package ru.spbau.blackout.progressbar;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import ru.spbau.blackout.utils.Textures;


// I know that libgdx has its own API for progress bars, but this one is much better in some cases.
// Also it's adapted for the project (project-specific methods like load and initialize).
/**
 * Provides easy and powerful API for creating progress bars.
 */
public abstract class SimpleProgressBar extends Actor {
    private static final float MAX_SPEED = 1f;


    private String fullTexturePath;
    private String emptyTexturePath;

    protected TextureRegionDrawable full;
    protected TextureRegionDrawable empty;

    private float minValue;
    private float maxValue;
    /** Real value from 0 to 1. */
    private float realValue;
    /** Value from 0 to 1 after interpolation. */
    protected float valueToShow = 0;


    protected SimpleProgressBar(SimpleProgressBar other) {
        this.fullTexturePath = other.fullTexturePath;
        this.emptyTexturePath = other.emptyTexturePath;
        if (other.full != null) {
            this.full = new TextureRegionDrawable(other.full.getRegion());
        }
        if (other.empty != null) {
            this.empty = new TextureRegionDrawable(other.empty.getRegion());
        }
        this.minValue = other.minValue;
        this.maxValue = other.maxValue;
        this.realValue = other.realValue;
        this.valueToShow = other.valueToShow;

        this.setSize(other.getWidth(), other.getHeight());
        this.setPosition(other.getX(), other.getY());

        int zIndex = other.getZIndex();
        if (zIndex >= 0) {
            this.setZIndex(zIndex);
        }
    }


    public SimpleProgressBar(String emptyTexturePath, String fullTexturePath, float minValue, float maxValue) {
        this.fullTexturePath = fullTexturePath;
        this.emptyTexturePath = emptyTexturePath;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }


    /**
     * Loads textures.
     * One can don't call this function if textures are already loaded.
     */
    public final void load(AssetManager assets) {
        Textures.loadMipMapAA(this.fullTexturePath, assets);
        Textures.loadMipMapAA(this.emptyTexturePath, assets);
    }


    public void doneLoading(AssetManager assets) {
        Texture fullTexture = assets.get(this.fullTexturePath, Texture.class);
        this.full = new TextureRegionDrawable(new TextureRegion(fullTexture));

        Texture emptyTexture = assets.get(this.emptyTexturePath, Texture.class);
        this.empty = new TextureRegionDrawable(new TextureRegion(emptyTexture));

        this.setValue(minValue);

        this.fullTexturePath = null;
        this.emptyTexturePath = null;
    }


    /**
     * Warning: some actor parameters may be not copied.
     */
    public abstract SimpleProgressBar copy();


    @Override
    public void act(float deltaTime) {
        super.act(deltaTime);

        float dValue = this.realValue - this.valueToShow;
        float maxDValue = MAX_SPEED * deltaTime;
        this.valueToShow += Math.min(Math.max(dValue, -maxDValue), maxDValue);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        empty.draw(batch, this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    public void setValue(float value) {
        this.realValue = (value - this.minValue) / this.maxValue;  // from 0 to 1
        if (this.realValue > 1) { this.realValue = 1; }
        if (this.realValue < 0) { this.realValue = 0; }
    }

    public void setValueInstant(float value) {
        this.setValue(value);
        this.valueToShow = this.realValue;
    }
}
