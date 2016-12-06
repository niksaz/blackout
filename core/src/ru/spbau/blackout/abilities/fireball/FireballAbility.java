package ru.spbau.blackout.abilities.fireball;

import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.abilities.SimpleInstantAbility;
import ru.spbau.blackout.entities.AbilityObject;
import ru.spbau.blackout.shapescreators.CircleCreator;


public class FireballAbility extends SimpleInstantAbility {
    public static final String ICON_PATH = "abilities/fireball/icon.png";
    public static final float MAX_CHARGE_TIME = 1f;

    public static final float SHELL_START_SPEED = 30f;
    public static final float SHELL_MASS = 5f;
    public static final float SHELL_RADIUS = 0.5f;
    public static final float CAST_DISTANCE = 1.5f;  // FIXME: use unit radius
    /** The estimated distance of the ability in case of no external force. */
    public static final float DISTANCE = 12f;
    public static final float TIME_TO_LIVE = DISTANCE / SHELL_START_SPEED;
    public static final float IMPULSE_FACTOR = 40f;

    public static final String FIRE_EFFECT_PATH = "abilities/fireball/particles/fireball.pfx";
    public static final String EXPLOSION_EFFECT_PATH = "effects/small_explosion/small_explosion.pfx";

    public static final String CAST_SOUND = "sounds/fire.ogg";


    private final FireballObject.Definition shellDef;


    public FireballAbility(int level) {
        super(level);
        this.shellDef = new FireballObject.Definition(null, new CircleCreator(SHELL_RADIUS), SHELL_MASS);
        this.shellDef.chestPivotOffset.set(0, 0, 1.5f);
        this.shellDef.isSensor = true;
        this.shellDef.timeToLive = TIME_TO_LIVE;
        this.shellDef.damage = 30f;  // FIXME
    }


    @Override
    public void cast() {
        Vector2 direction = new Vector2(1, 0).rotateRad(getUnit().getRotation());
        Vector2 position = new Vector2(getUnit().getPosition());
        position.mulAdd(direction, CAST_DISTANCE);
        AbilityObject shell = (AbilityObject) shellDef.makeInstance(position);
        shell.velocity.mulAdd(direction, SHELL_START_SPEED);
    }

    @Override
    public String castSoundPath() {
        return CAST_SOUND;
    }

    @Override
    public void load() {
        super.load();
        this.shellDef.load();
    }

    @Override
    public void doneLoading() {
        super.doneLoading();
        this.shellDef.doneLoading();
    }

    @Override
    public String iconPath() { return ICON_PATH; }
    @Override
    public float getMaxChargeTime() { return MAX_CHARGE_TIME; }
}
