package ru.spbau.blackout.graphiceffects;


import ru.spbau.blackout.GameContext;

/**
 * Ideologically <code>GraphicEffect</code> is something visual attached to something else
 * (most likely to a <code>GameObject</code>).
 */
public interface GraphicEffect {
    void remove(GameContext context);
    void update(float deltaTime);
}
