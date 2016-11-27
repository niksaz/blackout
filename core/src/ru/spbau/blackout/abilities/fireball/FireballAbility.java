package ru.spbau.blackout.abilities.fireball;

import com.badlogic.gdx.Gdx;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.InstantAbility;
import ru.spbau.blackout.entities.GameUnit;


public class FireballAbility extends InstantAbility {
    public FireballAbility(int level) {
        super(level);
    }

    @Override
    public void castBy(GameUnit unit) {
        Gdx.app.log("Blackout", "fireball cast");
    }

    @Override
    public String iconPath() {
        return "abilities/fireball/icon.png";
    }
}
