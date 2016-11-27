package ru.spbau.blackout.abilities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import ru.spbau.blackout.entities.GameUnit;


/**
 * Abstract class for ability in-game representation.
 * Each unit has its own instance of each its ability.
 */
public abstract class Ability {
    private final int level;
    private GameUnit unit;

    public Ability(int level) {
        this.level = level;
    }


    /**
     * Load necessary assets.
     * Must be called once from <code>GameUnit.Definition::update</code>
     */
    public void load(AssetManager assets) {
        assets.load(this.iconPath(), Texture.class);
    }
    /**
     * When assets are loaded.
     * Must be called once from <code>GameUnit::update</code>.
     */
    public void doneLoading(AssetManager assets, GameUnit unit) {
        this.unit = unit;
    }


    /** Called once when a touch goes down on the icon. */
    public abstract void onCastStart();
    /** Called each frame when the icon is pressed. */
    public abstract void onCastEnd();
    /** Called once when a touch goes up. */
    public abstract void inCast(float deltaTime);

    /** Path to the icon in assets directory. */
    public abstract String iconPath();


    public GameUnit getUnit() { return this.unit; }
}
