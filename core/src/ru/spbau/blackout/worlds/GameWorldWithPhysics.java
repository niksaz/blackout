package ru.spbau.blackout.worlds;

import java.util.Iterator;

import ru.spbau.blackout.entities.GameObject;

import static ru.spbau.blackout.java8features.Functional.foreach;

/**
 * GameWorld with physics computations. Used in a single-player game and on server during a multi-player game.
 */
public class GameWorldWithPhysics extends GameWorld {

    /** The fixed physic driver's step. */
    private static final float WORLD_STEP = 1 / 58f;

    private float accumulator = 0;

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for (GameObject object : this) {
            object.updateState(deltaTime);
        }

        for (Iterator<GameObject> it = this.iterator(); it.hasNext();) {
            GameObject object = it.next();
            if (object.isDead()) {
                object.destroyBody(this.box2dWorld);
                it.remove();
            }
        }

        accumulator += deltaTime;
        while (accumulator >= WORLD_STEP) {
            step();
            accumulator -= WORLD_STEP;
        }

        // I don't think that interpolation is necessary.
        // It would be very hard and takes many resources.
    }

    private void step() {
        stepNumber += 1;

        foreach(this, GameObject::updateForFirstStep);
        this.box2dWorld.step(WORLD_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

        foreach(this, GameObject::updateForSecondStep);
        this.box2dWorld.step(WORLD_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
    }
}
