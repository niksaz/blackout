package ru.spbau.blackout.effects;

import ru.spbau.blackout.entities.GameObject;

public class RotationEffect extends PhysicEffect {

    private final float radPerSecond;

    public RotationEffect(GameObject gameObject, float radPerSecond) {
        super(gameObject);
        this.radPerSecond = radPerSecond;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        gameObject.rotate(radPerSecond * deltaTime);
    }
}
