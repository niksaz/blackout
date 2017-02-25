package ru.spbau.blackout.progressbar;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;

public class VerticalProgressBar extends SimpleProgressBar {
    private /*final*/ int fullHeight;


    protected VerticalProgressBar(VerticalProgressBar other) {
        super(other);
        fullHeight = other.fullHeight;
    }

    public VerticalProgressBar(String emptyTexturePath, String fullTexturePath, float minValue, float maxValue) {
        super(emptyTexturePath, fullTexturePath, minValue, maxValue);
    }

    public VerticalProgressBar(String emptyTexturePath, String fullTexturePath) {
        this(emptyTexturePath, fullTexturePath, 0, 1);
    }


    @Override
    public void doneLoading(AssetManager assets) {
        super.doneLoading(assets);
        fullHeight = full.getRegion().getTexture().getHeight();
    }

    @Override
    public SimpleProgressBar copy() {
        return new VerticalProgressBar(this);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        int newHeight = (int) (valueToShow * fullHeight);
        // region coordinates start from left up corner
        full.getRegion().setRegionHeight(newHeight);
        full.getRegion().setRegionY(fullHeight - newHeight);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        full.draw(batch, getX(), getY(), getWidth(), valueToShow * getHeight());
    }
}
