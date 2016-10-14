package ru.spbau.blackout.entities;

public abstract class GameUnit extends DynamicObject {
    public GameUnit(String modelPath, float initialX, float initialY) {
        super(modelPath, initialX, initialY);
    }
}
