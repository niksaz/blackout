package ru.spbau.blackout.abilities;

import ru.spbau.blackout.entities.Hero;

/**
 * Abstract class for abilities which should be casted by single tap on the icon.
 */
public abstract class InstantAbility extends Ability {
    /**
     * Takes caster hero.
     */
    public abstract void castBy(Hero hero);
}
