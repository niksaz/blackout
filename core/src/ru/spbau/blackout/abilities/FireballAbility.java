package ru.spbau.blackout.abilities;

import com.badlogic.gdx.Gdx;

import ru.spbau.blackout.entities.Hero;

public class FireballAbility extends InstantAbility {
    public FireballAbility(Hero hero, int level) {
        super(hero, level);
    }

    @Override
    public void cast() {
        Gdx.app.log("Blackout.Ability.Cast", "begin");
        // TODO
    }

    @Override
    public String iconPath() {
        return "icons/abilities/fireball.png";
    }
}
