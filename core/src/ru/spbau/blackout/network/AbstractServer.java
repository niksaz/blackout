package ru.spbau.blackout.network;

import com.badlogic.gdx.math.Vector2;

/**
 * Interface which allows some entity to monitor user inputs and react to them.
 */
public interface AbstractServer {
    void sendSelfVelocity(Vector2 velocity);
}
