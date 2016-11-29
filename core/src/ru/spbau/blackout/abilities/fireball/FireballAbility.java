package ru.spbau.blackout.abilities.fireball;

import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.abilities.InstantAbility;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.entities.AbilityObject;
import ru.spbau.blackout.shapescreators.CircleCreator;


public class FireballAbility extends InstantAbility {
    public static final String ICON_PATH = "abilities/fireball/icon.png";
    public static final String MODEL_PATH = "abilities/fireball/model/fireball.g3db";
    public static final float MAX_CHARGE_TIME = 3f;

    public static final float SHELL_START_SPEED = 30f;
    public static final float CAST_DISTANCE = 1f;
    public static final float SHELL_MASS = 5f;
    /** The estimated distance of the ability in case of no external force. */
    public static final float DISTANCE = 12f;
    public static final float TIME_TO_LIVE = DISTANCE / SHELL_START_SPEED;


    private final AbilityObject.Definition shellDef;


    public FireballAbility(int level) {
        super(level);
        this.shellDef = new FireballObject.Definition(MODEL_PATH, new CircleCreator(1), SHELL_MASS, TIME_TO_LIVE);
    }


    @Override
    public void cast() {
        Vector2 direction = new Vector2(1, 0).rotateRad(getUnit().getRotation());
        AbilityObject shell = (AbilityObject) shellDef.makeInstance(
                getUnit().getPosition().mulAdd(direction, CAST_DISTANCE)
        );
        shell.velocity.mulAdd(direction, SHELL_START_SPEED);
    }


    @Override
    public void load(GameContext context) {
        super.load(context);
        shellDef.load(context);
    }

    @Override
    public void doneLoading(GameContext context, GameUnit unit) {
        super.doneLoading(context, unit);
        shellDef.doneLoading(context);
    }

    @Override
    public String iconPath() { return ICON_PATH; }
    @Override
    public float getMaxChargeTime() { return MAX_CHARGE_TIME; }
}
