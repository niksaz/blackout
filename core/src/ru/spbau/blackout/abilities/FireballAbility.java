package ru.spbau.blackout.abilities;

import ru.spbau.blackout.entities.Hero;

public class FireballAbility extends InstantAbility {
    public FireballAbility(Hero hero, int level) {
        super(hero, level);
    }

    @Override
    public void cast() {

    }

    @Override
    public String iconPath() {
        return "icons/abilities/fireball.png";
    }
}
