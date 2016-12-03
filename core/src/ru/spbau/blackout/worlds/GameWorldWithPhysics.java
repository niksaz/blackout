package ru.spbau.blackout.worlds;

import ru.spbau.blackout.entities.GameObject;

public class GameWorldWithPhysics extends GameWorld {

    private static final float WORLD_STEP = 1 / 58f;

    private float accumulator = 0;

    @Override
    public void update(float delta) {
        accumulator += delta;

        for (GameObject object : gameObjects) {
            object.updateState(delta);
        }

        while (accumulator >= WORLD_STEP) {
            step();
            accumulator -= WORLD_STEP;
        }

        // I don't think that interpolation is necessary.
        // It would be very hard and takes many resources.
    }

    private void step() {
//        TODO: gameObjects.forEach(GameObject::updateForFirstStep);
        for (GameObject object : this) {
            object.updateForFirstStep();
        }
        world.step(WORLD_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

//        TODO: gameObjects.forEach(GameObject::updateForSecondStep);
        for (GameObject object : this) {
            object.updateForSecondStep();
        }
        world.step(WORLD_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
    }
}
