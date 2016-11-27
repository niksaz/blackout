package ru.spbau.blackout.abilities;

import ru.spbau.blackout.entities.Hero;

/**
 * Abstract class for abilities which should be casted by holding tap on the icon for some time.
 */
public abstract class HoldableAbility extends Ability {
    /**
     * Takes caster hero and duration of holding the icon.
     */
    public abstract void castBy(Hero hero, float duration);
}
