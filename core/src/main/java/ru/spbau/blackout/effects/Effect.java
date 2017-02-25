package ru.spbau.blackout.effects;

import ru.spbau.blackout.entities.GameObject;

public abstract class Effect {

    protected final GameObject gameObject;

    protected Effect(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public abstract void remove();
    public void dispose() {}
    public void update(float deltaTime) {}
}
