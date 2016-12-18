package ru.spbau.blackout.network;

import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.entities.GameUnit;

public class Events {

    static public void abilityCast(GameUnit unit, int abilityNum, Vector2 target) {
        unit.castAbility(abilityNum, target);
    }
}
