package ru.spbau.blackout.progressbar;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import ru.spbau.blackout.utils.Textures;


// I know that libgdx has its own API for progress bars, but this one is much better in some cases.
// Also it's adapted for the project (project-specific methods like load and initializeGameWorld).
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
        fullTexturePath = other.fullTexturePath;
        emptyTexturePath = other.emptyTexturePath;
        if (other.full != null) {
            full = new TextureRegionDrawable(other.full.getRegion());
        }
        if (other.empty != null) {
            empty = new TextureRegionDrawable(other.empty.getRegion());
        }
        minValue = other.minValue;
        maxValue = other.maxValue;
        realValue = other.realValue;
        valueToShow = other.valueToShow;

        setSize(other.getWidth(), other.getHeight());
        setPosition(other.getX(), other.getY());

        int zIndex = other.getZIndex();
        if (zIndex >= 0) {
            setZIndex(zIndex);
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
        Textures.loadMipMapAA(fullTexturePath, assets);
        Textures.loadMipMapAA(emptyTexturePath, assets);
    }


    public void doneLoading(AssetManager assets) {
        Texture fullTexture = assets.get(fullTexturePath, Texture.class);
        full = new TextureRegionDrawable(new TextureRegion(fullTexture));

        Texture emptyTexture = assets.get(emptyTexturePath, Texture.class);
        empty = new TextureRegionDrawable(new TextureRegion(emptyTexture));

        setValue(minValue);

        fullTexturePath = null;
        emptyTexturePath = null;
    }


    /**
     * Warning: some actor parameters may be not copied.
     */
    public abstract SimpleProgressBar copy();


    @Override
    public void act(float delta) {
        super.act(delta);

        float dValue = realValue - valueToShow;
        float maxDValue = MAX_SPEED * delta;
        valueToShow += Math.min(Math.max(dValue, -maxDValue), maxDValue);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        empty.draw(batch, getX(), getY(), getWidth(), getHeight());
    }

    public void setValue(float value) {
        realValue = (value - minValue) / maxValue;  // from 0 to 1
        if (realValue > 1) { realValue = 1; }
        if (realValue < 0) { realValue = 0; }
    }

    public void setValueInstant(float value) {
        setValue(value);
        valueToShow = realValue;
    }
}
