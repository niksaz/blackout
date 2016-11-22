package ru.spbau.blackout.network;

import com.badlogic.gdx.math.Vector2;

public interface AbstractServer {
    void sendSelfVelocity(Vector2 velocity);
}
