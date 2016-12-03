package ru.spbau.blackout.effects;

import ru.spbau.blackout.entities.GameObject;


/**
 * Represents a temporary effect on a <code>GameObject</code>. In most cases created by abilities.
 */
public abstract class GameEffect {
    protected final GameObject object;


    public GameEffect(GameObject object) {
        this.object = object;
    }


    // Just common methods
    public void dispose() {}
    public void update(float deltaTime) {}
}
