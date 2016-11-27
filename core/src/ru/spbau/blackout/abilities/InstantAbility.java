package ru.spbau.blackout.abilities;


import ru.spbau.blackout.entities.GameUnit;

/**
 * Abstract class for abilities which should be casted by single tap on the icon.
 */
public abstract class InstantAbility extends Ability {
    public InstantAbility(int level) {
        super(level);
    }

    public abstract void castBy(GameUnit unit);

    @Override
    public final void onCastStart(GameUnit unit) {
        this.castBy(unit);
    }
    @Override
    public final void inCast(GameUnit unit, float deltaTime) { /*nothing*/ }
    @Override
    public final void onCastEnd(GameUnit unit) { /*nothing*/ }
}
