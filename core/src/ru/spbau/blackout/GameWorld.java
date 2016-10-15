package ru.spbau.blackout;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

import ru.spbau.blackout.entities.GameObject;

public class GameWorld implements Iterable<GameObject> {

    public static final float WORLD_STEP = 1 / 30f;
    public static final int VELOCITY_ITERATIONS = 6;
    public static final int POSITION_ITERATIONS = 2;

    private final Array<GameObject> gameObjects = new Array<>();
    private final World world;
    private float accumulator = 0;

    public GameWorld() {
        // without gravity, without sleeping
        world = new World(Vector2.Zero, false);
    }

    @Override
    public Iterator<GameObject> iterator() {
        return gameObjects.iterator();
    }

    public void update(float delta) {
        accumulator += delta;

        while (accumulator >= WORLD_STEP) {
            step();
            accumulator -= WORLD_STEP;
        }

        // I don't think that interpolation is necessary.
        // It would be very hard and takes many resources.
    }

    public Body addObject(GameObject object, GameObject.Definition def) {
        gameObjects.add(object);
        return def.addToWorld(world);
    }

    private void step() {
        for (GameObject object : gameObjects) {
            object.update(WORLD_STEP);
        }

        world.step(WORLD_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
    }
}
