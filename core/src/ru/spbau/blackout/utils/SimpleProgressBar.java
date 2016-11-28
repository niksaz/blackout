package ru.spbau.blackout.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


// I know that libgdx has its own API for progress bars, but this one is much better in some cases.
/**
 * Provides easy and powerful API for creating progress bars.
 */
public class SimpleProgressBar extends Actor {
    private final String fullTexturePath;
    private final String emptyTexturePath;

    private TextureRegionDrawable full;
    private TextureRegionDrawable empty;

    public final Vector2 position = new Vector2();
    public final Vector2 size = new Vector2();

    private /*final*/ int fullWidth;
    private float minValue;
    private float maxValue;
    private float value;


    public SimpleProgressBar(String fullTexturePath, String emptyTexturePath, float minValue, float maxValue) {
        this.fullTexturePath = fullTexturePath;
        this.emptyTexturePath = emptyTexturePath;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.value = minValue;
    }

    public SimpleProgressBar(String fullTexturePath, String emptyTexturePath) {
        this(fullTexturePath, emptyTexturePath, 0, 1);
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
        this.setValue(minValue);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float normalized = (this.value - this.minValue) / this.maxValue;  // from 0 to 1
        this.full.getRegion().setRegionWidth((int) Math.floor(normalized * this.fullWidth));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        full.draw(batch, position.x, position.y, size.x, size.y);
        empty.draw(batch, position.x, position.y, size.x, size.y);
    }

    public void setValue(float value) { this.value = value; }
}
