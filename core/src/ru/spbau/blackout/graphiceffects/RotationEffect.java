package ru.spbau.blackout.graphiceffects;

import ru.spbau.blackout.entities.GameObject;

/**
 * WARNING: in opposite to the majority of graphic effects, this one affects physics.
 */
public final class RotationEffect extends GraphicEffect {

    private final float radPerSecond;

    public static void create(GameObject gameObject, float radPerSecond) {
        gameObject.getGraphicEffects().add(new RotationEffect(gameObject, radPerSecond));
    }

    private RotationEffect(GameObject gameObject, float radPerSecond) {
        super(gameObject);
        this.radPerSecond = radPerSecond;
    }

    @Override
    public void update(float deltaTime) {
        gameObject.rotate(radPerSecond * deltaTime);
    }
}
