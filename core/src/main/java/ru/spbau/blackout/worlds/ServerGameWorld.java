package ru.spbau.blackout.worlds;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.serializationutils.EffectiveOutputStream;
import ru.spbau.blackout.sessionsettings.SessionSettings;
import ru.spbau.blackout.utils.Uid;
import ru.spbau.blackout.utils.UidGenerator;

import static ru.spbau.blackout.java8features.Functional.foreach;


/**
 * GameWorld with physics computations. Used in a single-player game and on server during a multi-player game.
 */
public class ServerGameWorld extends GameWorld {

    /** The fixed physic driver's step. */
    private static final float WORLD_STEP = 1 / 58f;

    private float accumulator = 0;
    public final UidGenerator uidGenerator;


    public ServerGameWorld(SessionSettings sessionSettings) {
        super(sessionSettings.getDefinitions());
        uidGenerator = sessionSettings.getUidGenerator();
    }

    public ServerGameWorld(SessionSettings sessionSettings, GameContext context) {
        this(sessionSettings);
        foreach(getDefinitions(), def -> def.initializeWithoutUi(context));
    }

    public void getState(EffectiveOutputStream out) throws IOException {
        out.writeLong(stepNumber);

        out.writeInt(getGameObjects().size());

        for (GameObject go : getGameObjects()) {
            out.writeObject(go.getUid());
            out.writeInt(go.getDef().getDefNumber());
            go.getState(out);
        }
    }

    @Override
    public void updatePhysics(float delta) {
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

        for (GameObject object : getGameObjects()) {
            object.updateState(WORLD_STEP);
        }

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
