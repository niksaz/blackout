package ru.spbau.blackout.network;

import com.badlogic.gdx.math.Vector2;

/**
 * Server used for single player and it just does nothing in response to user inputs because
 * state is first synchronized with local GameWorld and it is enough for single player.
 */
public class IdleServer implements AbstractServer {
    @Override
    public void sendSelfVelocity(Vector2 velocity) {}
}
