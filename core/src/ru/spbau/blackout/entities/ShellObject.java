package ru.spbau.blackout.entities;

import com.badlogic.gdx.math.Vector2;

public class ShellObject extends DynamicObject {
    public ShellObject(String modelPath, float initialX, float initialY) {
        super(modelPath, initialX, initialY);
    }

    public ShellObject(String modelPath, Vector2 initialPosition) {
        this(modelPath, initialPosition.x, initialPosition.y);
    }

    public ShellObject(String modelPath) {
        this(modelPath, 0, 0);
    }
}
