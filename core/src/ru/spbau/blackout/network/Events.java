package ru.spbau.blackout.network;

import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.entities.GameUnit;

public class Events {

    public static void setSelfVelocity(GameUnit unit, Vector2 velocity) {
        unit.setSelfVelocity(velocity);
    }

    public static void abilityCast(GameUnit unit, int abilityNum, Vector2 target) {
        unit.castAbility(abilityNum, target);
    }
}
