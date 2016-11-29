package ru.spbau.blackout.abilities;


import ru.spbau.blackout.entities.GameUnit;

/**
 * Abstract class for abilities which should be casted by single tap on the icon.
 */
public abstract class InstantAbility extends Ability {
    public InstantAbility(int level) {
        super(level);
    }

    public abstract void cast();

    @Override
    public final void onCastStart() {
        this.cast();
        this.chargeStart();
    }
    @Override
    public final void inCast(float deltaTime) { /*nothing*/ }
    @Override
    public final void onCastEnd() { /*nothing*/ }
}
