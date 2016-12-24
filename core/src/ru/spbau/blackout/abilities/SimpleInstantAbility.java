package ru.spbau.blackout.abilities;


import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.network.UIServer;


/**
 * Abstract class for abilities which should be casted by single tap on the icon.
 */
public abstract class SimpleInstantAbility extends Ability {

    public static final float CAST_DISTANCE = 1.5f;  // FIXME: use unit radius


    public SimpleInstantAbility(int level) {
        super(level);
    }


    @Override
    public final void onCastStart(UIServer server) {
        Vector2 targetOffset = new Vector2(CAST_DISTANCE, 0).rotateRad(getUnit().getRotation());
        server.sendAbilityCast(getUnit(), getUnit().getAbilityNum(this), targetOffset);
    }

    @Override
    public final void inCast(UIServer server, float delta) { /* nothing */ }

    @Override
    public final void onCastEnd(UIServer server) { /*nothing*/ }
}
