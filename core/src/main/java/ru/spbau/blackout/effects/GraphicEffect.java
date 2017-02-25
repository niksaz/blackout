package ru.spbau.blackout.effects;


import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.GameObject;

/**
 * Ideologically <code>GraphicEffect</code> is something visual attached to something else
 * (most likely to a <code>GameObject</code>).
 */
public abstract class GraphicEffect extends Effect {

    public GraphicEffect(GameObject gameObject) {
        super(gameObject);
        gameObject.getGraphicEffects().add(this);
    }

    public final void remove() {
        gameObject.getGraphicEffects().remove(this);
        dispose();
    }
}
