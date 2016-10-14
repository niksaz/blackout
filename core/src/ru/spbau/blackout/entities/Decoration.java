package ru.spbau.blackout.entities;

import com.badlogic.gdx.math.Vector2;

public class Decoration extends GameObject {
    public Decoration(String modelPath, float initialX, float initialY) {
        super(modelPath, initialX, initialY);
    }

    public Decoration(String modelPath, Vector2 initialPosition) {
        this(modelPath, initialPosition.x, initialPosition.y);
    }

    public Decoration(String modelPath) {
        this(modelPath, 0, 0);
    }
}
