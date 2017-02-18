package ru.spbau.blackout.progressbar;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class HorizontalProgressBar extends SimpleProgressBar {
    private /*final*/ int fullWidth;


    protected HorizontalProgressBar(HorizontalProgressBar other) {
        super(other);
        fullWidth = other.fullWidth;
    }

    public HorizontalProgressBar(String emptyTexturePath, String fullTexturePath, float minValue, float maxValue) {
        super(emptyTexturePath, fullTexturePath, minValue, maxValue);
    }

    public HorizontalProgressBar(String emptyTexturePath, String fullTexturePath) {
        this(emptyTexturePath, fullTexturePath, 0, 1);
    }


    @Override
    public void doneLoading(AssetManager assets) {
        super.doneLoading(assets);
        fullWidth = full.getRegion().getTexture().getWidth();
    }


    @Override
    public SimpleProgressBar copy() {
        return new HorizontalProgressBar(this);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        full.getRegion().setRegionWidth(Math.round(valueToShow * fullWidth));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        full.draw(batch, getX(), getY(), valueToShow * getWidth(), getHeight());
    }
}
