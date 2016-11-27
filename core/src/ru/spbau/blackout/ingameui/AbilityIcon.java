package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.network.AbstractServer;
import ru.spbau.blackout.units.Rpx;

public class AbilityIcon extends DragListener {
    private final Settings settings;
    private Hero hero;
    private Image icon;

    public AbilityIcon(AbstractServer server, Settings settings) {
        this.settings = settings;
        // TODO: settings
    }

    public void load(AssetManager assets) {
        assets.load(this.getAbility().iconPath(), Texture.class);
    }

    public void doneLoading(AssetManager assets, Stage stage, Hero hero) {
        this.hero = hero;

        // icon initialization
        this.icon = new Image(assets.get(this.getAbility().iconPath(), Texture.class));
        this.icon.setSize(this.settings.getSizeX(), this.settings.getSizeY());
        this.icon.setPosition(settings.getStartX(), settings.getStartY());
        stage.addActor(this.icon);
        this.icon.addListener(this.getAbility().getIconInputListener());
    }

    public Ability getAbility() {
        return this.settings.ability;
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

        private Ability ability;
        public Ability getAbility() { return this.ability; }
        public void setAbility(Ability ability) { this.ability = ability; }

        private int sizeX = Rpx.X.fromCm(Defaults.SIZE_CM);
        public int getSizeX() { return this.sizeX; }
        public void setSizeX(int sizeX) { this.sizeX = sizeX; }

        private int sizeY = Rpx.Y.fromCm(Defaults.SIZE_CM);
        public int getSizeY() { return this.sizeY; }
        public void setSizeY(int sizeY) { this.sizeY = sizeY; }

        private int startX;
        public int getStartX() { return this.startX; }
        public void setStartX(int startX) { this.startX = startX; }

        private int startY;
        public int getStartY() { return this.startY; }
        public void setStartY(int startY) { this.startY = startY; }
    }
}
