package ru.spbau.blackout.graphiceffects;


import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.GameObject;

/**
 * Ideologically <code>GraphicEffect</code> is something visual attached to something else
 * (most likely to a <code>GameObject</code>).
 */
public abstract class GraphicEffect {

    protected final GameObject gameObject;

    protected GraphicEffect(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public final void remove() {
        gameObject.getGraphicEffects().remove(this);
        dispose();
    }

    public void dispose() {}

    public abstract void update(float deltaTime);
}
