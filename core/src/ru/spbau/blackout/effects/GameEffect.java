package ru.spbau.blackout.effects;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.GameObject;


/**
 * Represents a temporary effect on a <code>GameObject</code>. In most cases created by abilities.
 */
public abstract class GameEffect {
    // Just common methods
    public void load(GameContext context) {}
    public void doneLoading(GameContext context) {}
    public void dispose() {}
    public void update(float deltaTime, GameObject object) {}
}
