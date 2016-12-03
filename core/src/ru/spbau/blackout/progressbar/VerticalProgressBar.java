package ru.spbau.blackout.progressbar;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;

public class VerticalProgressBar extends SimpleProgressBar {
    private /*final*/ int fullHeight;


    public VerticalProgressBar(String emptyTexturePath, String fullTexturePath, float minValue, float maxValue) {
        super(emptyTexturePath, fullTexturePath, minValue, maxValue);
    }

    public VerticalProgressBar(String emptyTexturePath, String fullTexturePath) {
        this(emptyTexturePath, fullTexturePath, 0, 1);
    }


    @Override
    public void doneLoading(AssetManager assets) {
        super.doneLoading(assets);
        this.fullHeight = this.full.getRegion().getTexture().getHeight();
    }


    @Override
    public void act(float deltaTime) {
        super.act(deltaTime);
        int newHeight = (int) (this.valueToShow * this.fullHeight);
        // region coordinates start from left up corner
        this.full.getRegion().setRegionHeight(newHeight);
        this.full.getRegion().setRegionY(this.fullHeight - newHeight);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        this.full.draw(batch, this.getX(), this.getY(), this.getWidth(), this.valueToShow * this.getHeight());
    }
}
