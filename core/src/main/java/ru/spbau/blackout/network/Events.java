package ru.spbau.blackout.network;

import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.GameUnit;

public class Events {

    public static void setSelfVelocity(GameUnit unit, Vector2 velocity) {
        unit.setSelfVelocity(velocity);
    }

    public static void abilityCast(Character character, int abilityNum, Vector2 target) {
        character.castAbility(abilityNum, target);
    }

    public static GameUnit playerDeath(long uid) {
        return null;
        // TODO: create ghost
    }
}
