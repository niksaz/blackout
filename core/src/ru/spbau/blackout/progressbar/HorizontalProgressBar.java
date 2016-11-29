package ru.spbau.blackout.progressbar;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public class HorizontalProgressBar extends SimpleProgressBar {
    private /*final*/ int fullWidth;


    public HorizontalProgressBar(String emptyTexturePath, String fullTexturePath, float minValue, float maxValue) {
        super(emptyTexturePath, fullTexturePath, minValue, maxValue);
    }

    public HorizontalProgressBar(String emptyTexturePath, String fullTexturePath) {
        this(emptyTexturePath, fullTexturePath, 0, 1);
    }


    @Override
    public void doneLoading(AssetManager assets) {
        super.doneLoading(assets);
        this.fullWidth = this.full.getRegion().getTexture().getWidth();
    }


    @Override
    public void act(float deltaTime) {
        super.act(deltaTime);
        this.full.getRegion().setRegionWidth(Math.round(this.valueToShow * this.fullWidth));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        this.full.draw(batch, this.getX(), this.getY(), this.valueToShow * this.getWidth(), this.getHeight());
    }
}
