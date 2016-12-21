package ru.spbau.blackout.graphiceffects;


/**
 * Ideologically <code>GraphicEffect</code> is something visual attached to something else
 * (most likely to a <code>GameObject</code>).
 */
public interface GraphicEffect {
    void remove();
    void update(float deltaTime);
}
