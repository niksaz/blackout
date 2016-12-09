package ru.spbau.blackout.worlds;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.FrictionJoint;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ru.spbau.blackout.BlackoutContactListener;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.utils.InplaceSerializable;

import static ru.spbau.blackout.java8features.Functional.foreach;


/**
 * Main class for all in-game computation including physics.
 * Isn't connected with any graphics or audio in order to be able to be run on server.
 * Also used to send information about the game state from the server to clients.
 *
 *
 * <p>Physics system:
 * <br>Due to box2d limitations there is a complex system around it based on two steps.
 *
 * <br>Physic driver used with the fixed step (see WORLD_STEP static field) and without interpolation.
 *
 * <br>The first step is when external velocity is processed. In <code>updateForFirstStep</code> method
 * each object must set its external velocity as its body's velocity.
 *
 * <br>In <code>updateForSecondStep</code> method each object must getOriginal its new external velocity from
 * its body's velocity and then it must put its own velocity (just like <code>selfVelocity</code> of GameUnit) instead.
 * The resulting velocity of the second step isn't important.
 *
 * <br>There is one more method called <code>updateState</code>. It must update things which are not connected
 * with physic driver. This method called one time per frame (i.e. without fixed step).
 */
public abstract class GameWorld implements Iterable<GameObject>, InplaceSerializable {
    /** The fixed physic driver's step. */
    public static final int VELOCITY_ITERATIONS = 1;
    public static final int POSITION_ITERATIONS = 2;

    private final List<GameObject> gameObjects = new LinkedList<>();
    transient protected final World box2dWorld;


    public GameWorld() {
        // without gravity, without sleeping
        this.box2dWorld = new World(Vector2.Zero, false);
        this.box2dWorld.setContactListener(new BlackoutContactListener());

        {
            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.StaticBody;
            def.position.set(0, 0);
        }

        {
            CircleShape shape = new CircleShape();
            shape.setRadius(10000f); // infinity radius

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.isSensor = true; // no collisions with the ground

            shape.dispose();
        }
    }

    public List<GameObject> getGameObjects() {
        return this.gameObjects;
    }

    @Override
    public Iterator<GameObject> iterator() {
        return this.gameObjects.iterator();
    }

    @Override
    public void inplaceSerialize(ObjectOutputStream out) throws IOException, ClassNotFoundException {
        out.writeInt(gameObjects.size());

        for (GameObject object : this) {
            object.inplaceSerialize(out);
        }
    }

    @Override
    public Object inplaceDeserialize(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.readInt();

        for (GameObject object : this) {
            object.inplaceDeserialize(in);
        }

        return null;
    }

    public void update(float delta) {
        /// common things
    }

    public Body addObject(GameObject object, BodyDef bodyDef) {
        this.gameObjects.add(object);
        return this.box2dWorld.createBody(bodyDef);
    }

    public void dispose() {
        this.box2dWorld.dispose();
        for (GameObject object : this) {
            object.dispose();
        }
    }
}

