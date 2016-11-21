package ru.spbau.blackout.network;

import com.badlogic.gdx.math.Vector2;

public abstract class AbstractServer {
    abstract public void sendSelfVelocity(Vector2 direction);
}
