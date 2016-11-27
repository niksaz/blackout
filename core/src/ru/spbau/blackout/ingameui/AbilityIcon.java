package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.units.Rpx;

public class AbilityIcon {
    private final Ability ability;
    private final Settings settings;
    private Hero hero;
    private Image icon;


    public AbilityIcon(Ability ability, Settings settings) {
        this.ability = ability;
        this.settings = settings;
        // TODO: settings
    }

    public void load(AssetManager assets) {
        assets.load(this.ability.getIconPath(), Texture.class);
    }

    public void doneLoading(AssetManager assets, Stage stage, Hero hero) {
        this.hero = hero;

        // icon initialization
        this.icon = new Image(assets.get(this.ability.getIconPath(), Texture.class));
        this.icon.setSize(this.settings.getSizeX(), this.settings.getSizeY());
        this.icon.setPosition(settings.getStartX(), settings.getStartY());
        stage.addActor(this.icon);
//        this.icon.addListener(this);
    }

    /**
     * Contains some settings for displaying of this UI object.
     * All getters and setters work with RPX.
     */
    public static class Settings {
        public static class Defaults {
            private Defaults() {}
            public static final float SIZE_CM = 1f;
        }

        public Settings(int startX, int startY) {
            this.startX = startX;
            this.startY = startY;
        }

        private int sizeX = Rpx.X.fromCm(Defaults.SIZE_CM);
        public int getSizeX() { return sizeX; }
        public void setSizeX(int sizeX) { this.sizeX = sizeX; }

        private int sizeY = Rpx.Y.fromCm(Defaults.SIZE_CM);
        public int getSizeY() { return sizeY; }
        public void setSizeY(int sizeY) { this.sizeY = sizeY; }

        private int startX;
        public int getStartX() { return startX; }
        public void setStartX(int startX) { this.startX = startX; }

        private int startY;
        public int getStartY() { return startY; }
        public void setStartY(int startY) { this.startY = startY; }
    }
}
