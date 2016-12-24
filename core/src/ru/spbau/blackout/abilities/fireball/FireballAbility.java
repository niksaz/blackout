package ru.spbau.blackout.abilities.fireball;

import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.abilities.SimpleInstantAbility;
import ru.spbau.blackout.entities.AbilityObject;
import ru.spbau.blackout.shapescreators.CircleCreator;


public class FireballAbility extends SimpleInstantAbility {

    private static final long serialVersionUID = 1000000000L;

    private static final String ICON_PATH = "abilities/fireball/icon.png";
    private static final float MAX_CHARGE_TIME = 1f;

    private static final float SHELL_START_SPEED = 30f;
    private static final float SHELL_MASS = 5f;
    private static final float SHELL_RADIUS = 0.5f;
    /** The estimated distance of the ability in case of no external force. */
    private static final float DISTANCE = 12f;
    private static final float TIME_TO_LIVE = DISTANCE / SHELL_START_SPEED;


    private final FireballObject.Definition shellDef;


     public FireballAbility(int level) {
        super(level);
        this.shellDef = new FireballObject.Definition(null, new CircleCreator(SHELL_RADIUS), SHELL_MASS);
        this.shellDef.chestPivotOffset.set(0, 0, 1.5f);
        this.shellDef.isSensor = true;
        this.shellDef.timeToLive = TIME_TO_LIVE;
        this.shellDef.damage = 20.0f + 5.0f * level;
    }


    @Override
    public void cast(Vector2 targetOffset) {
        super.cast(targetOffset);

        Vector2 unitPosition = getUnit().getPosition();
        targetOffset.add(unitPosition);  // real target
        AbilityObject shell = (AbilityObject) shellDef.makeInstanceWithNextUid(targetOffset);
        targetOffset.sub(unitPosition);

        targetOffset.scl(SHELL_START_SPEED / targetOffset.len());  // start speed
        shell.velocity.add(targetOffset);
    }

    @Override
    public void load(GameContext context) {
        super.load(context);
        this.shellDef.load(context);
    }

    @Override
    public void doneLoading(GameContext context) {
        super.doneLoading(context);
        this.shellDef.doneLoading();
    }

    @Override
    public String iconPath() { return ICON_PATH; }
    @Override
    public float getMaxChargeTime() { return MAX_CHARGE_TIME; }
}
