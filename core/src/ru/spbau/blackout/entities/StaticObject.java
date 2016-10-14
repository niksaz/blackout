package ru.spbau.blackout.entities;

import com.badlogic.gdx.math.Vector2;

public abstract class StaticObject extends GameObject {
    public StaticObject(String modelPath, float initialX, float initialY) {
        super(modelPath, initialX, initialY);
    }
}
