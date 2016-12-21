package ru.spbau.blackout.abilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.network.UIServer;


/**
 * Abstract class for ability in-game representation.
 * Each unit has its own instance of each its ability.
 */
public abstract class Ability implements Serializable {
    private final int level;
    private transient float chargeTime;

    private transient /*final*/ GameUnit unit;


    public Ability(int level) {
        this.level = level;
    }



    /**
     * Load necessary assets.
     * Must be called once from <code>GameUnit.Definition::update</code>
     */
    public void load(GameContext context) {
        // loading of icon has to be here because it isn't accessible from `AbilityIcon` class in the loading stage
        context.getAssets().load(iconPath(), Texture.class);
    }

    public void doneLoading(GameContext context) {}

    /**
     * Must be called exactly once from <code>GameUnit::new</code>.
     */
    public void initialize(GameUnit unit) {
        this.unit = unit;
    }


    /** Called once when a touch goes down on the icon. */
    public abstract void onCastStart(UIServer server);
    /** Called each frame when the icon is pressed. */
    public abstract void onCastEnd(UIServer server);
    /** Called once when a touch goes up. */
    public abstract void inCast(UIServer server, float delta);

    public abstract void cast(Vector2 target);

    /** Path to the icon in assets directory. */
    public abstract String iconPath();

    public abstract float getMaxChargeTime();

    public GameUnit getUnit() { return unit; }
    public float getChargeTime() { return chargeTime; }
    public void setChargeTime(float chargeTime) { this.chargeTime = chargeTime; }
    public void charge(float deltaTime) {
        chargeTime -= Math.min(deltaTime, chargeTime);
    }

    /** Sets current charge time as <code>maxChargeTime</code>. */
    public void chargeStart() { setChargeTime(getMaxChargeTime()); }
}
