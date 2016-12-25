package ru.spbau.blackout.worlds;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.sessionsettings.SessionSettings;

import static ru.spbau.blackout.java8features.Functional.foreach;


/**
 * GameWorld with physics computations. Used in a single-player game and on server during a multi-player game.
 */
public class ServerGameWorld extends GameWorld {

    /** The fixed physic driver's step. */
    private static final float WORLD_STEP = 1 / 58f;

    private float accumulator = 0;
    private long lastUid;


    public ServerGameWorld(SessionSettings sessionSettings) {
        super(sessionSettings.getDefinitions());
        lastUid = sessionSettings.getLastUid();
    }

    public ServerGameWorld(SessionSettings sessionSettings, GameContext context) {
        this(sessionSettings);
        foreach(getDefinitions(), def -> def.initializeWithoutUi(context));
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

    public long getNextUid() {
        lastUid += 1;
        return lastUid;
    }

    @Override
    public void updateState(float delta) {
        for (GameObject object : getGameObjects()) {
            object.updateState(delta);
        }

        for (Iterator<GameObject> it = getGameObjects().iterator(); it.hasNext();) {
            GameObject object = it.next();
            if (object.isDead()) {
                removeDeadObject(object);
                it.remove();
            }
        }

        accumulator += delta;
        while (accumulator >= WORLD_STEP) {
            step();
            accumulator -= WORLD_STEP;
        }

        updateGraphics(delta);
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
