package ru.spbau.blackout.network;

import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.GameUnit;

/**
 * Interface which allows some entity to monitor user inputs and react to them.
 */
public interface UIServer {

    void sendSelfVelocity(GameUnit unit, Vector2 velocity);
    void sendAbilityCast(Character character, int abilityNum, Vector2 targetOffset);
    void waitForOtherPlayers();
}
