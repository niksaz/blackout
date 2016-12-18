package ru.spbau.blackout.network;

import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.entities.GameUnit;

/**
 * Server used for single player and it just does nothing in response to user inputs because
 * state is first synchronized with local GameWorld and it is enough for single player.
 */
public class SinglePlayerServer implements UIServer {

    @Override
    public void sendSelfVelocity(GameUnit unit, Vector2 velocity) {
        unit.setSelfVelocity(velocity);
    }

    @Override
    public void sendAbilityCast(GameUnit unit, int abilityNum, Vector2 target) {
        Events.abilityCast(unit, abilityNum, target);
    }
}
