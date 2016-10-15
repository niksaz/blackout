package ru.spbau.blackout;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class Physics {
    World world;

    public static final float WORLD_STEP = 1 / 30f;
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 2;

    private float accumulator = 0;

    public Physics() {
        // without gravity, with sleeping
        world = new World(Vector2.Zero, true);
    }

    public void update(float delta) {
        accumulator += delta;

        // TODO: interpolation
        while (accumulator >= WORLD_STEP) {
            world.step(WORLD_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= WORLD_STEP;
        }
    }

    public World getWorld() {
        return world;
    }
}
