package ru.spbau.blackout.worlds;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;

import ru.spbau.blackout.entities.GameObject;

import static ru.spbau.blackout.java8features.Functional.foreach;


/**
 * GameWorld with physics computations. Used in a single-player game and on server during a multi-player game.
 */
public class ServerGameWorld extends GameWorld {

    /** The fixed physic driver's step. */
    private static final float WORLD_STEP = 1 / 58f;

    private float accumulator = 0;


    public ServerGameWorld(List<GameObject.Definition> definitions) {
        super(definitions);
    }


    public void getState(ObjectOutputStream out) throws IOException, ClassNotFoundException {
        out.writeLong(stepNumber);

        out.writeInt(getGameObjects().size());

        for (GameObject go : getGameObjects()) {
            out.writeLong(go.getUid());
            out.writeInt(go.getDef().getDefNumber());
            go.getState(out);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        for (GameObject object : getGameObjects()) {
            object.updateState(deltaTime);
        }

        for (Iterator<GameObject> it = getGameObjects().iterator(); it.hasNext();) {
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

        foreach(getGameObjects(), GameObject::updateForFirstStep);
        this.box2dWorld.step(WORLD_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

        foreach(getGameObjects(), GameObject::updateForSecondStep);
        this.box2dWorld.step(WORLD_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
    }

    @Override
    public Body addObject(GameObject object, BodyDef bodyDef) {
        return super.addObject(object, bodyDef);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
