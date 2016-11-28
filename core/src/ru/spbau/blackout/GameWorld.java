package ru.spbau.blackout;


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

import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.utils.InplaceSerializable;

import static ru.spbau.blackout.java8features.Functional.foreach;


/**
 * Main class for all in-game computation including physics.
 * Isn't connected with any graphics or audio in order to be able to be run on server.
 * Also used to send information about the game state from the server to clients.
 *
 * <p>Physics system:
 * <br>Due to box2d limitations there is a complex system around it based on two steps.
 * TODO: describe two(three) physics steps.
 */
public class GameWorld implements Iterable<GameObject>, InplaceSerializable {
    public static final float WORLD_STEP = 1 / 58f;
    public static final int VELOCITY_ITERATIONS = 1;
    public static final int POSITION_ITERATIONS = 2;

    // TODO: UID
    private final List<GameObject> gameObjects = new LinkedList<>();
    transient private final World world;
    transient private float accumulator = 0;
    transient private Body ground;

    public GameWorld() {
        // without gravity, without sleeping
        world = new World(Vector2.Zero, false);

        {
            BodyDef def = new BodyDef();
            def.type = BodyDef.BodyType.StaticBody;
            def.position.set(0, 0);
            ground = world.createBody(def);
        }

        {
            CircleShape shape = new CircleShape();
            shape.setRadius(10000f); // infinity radius

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.isSensor = true; // no collisions with the ground

            ground.createFixture(fixtureDef);

            shape.dispose();
        }
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    @Override
    public Iterator<GameObject> iterator() {
        return gameObjects.iterator();
    }

    @Override
    public synchronized void inplaceSerialize(ObjectOutputStream out) throws IOException, ClassNotFoundException {
        out.writeInt(gameObjects.size());

        for (GameObject object : this) {
            //object.inplaceSerialize(out);

            //long curTime = System.currentTimeMillis();
            //out.writeLong(curTime);

            Vector2 ob = object.getPosition();
            out.writeObject(ob);
            //Vector2 v2 = ;//new Vector2(System.currentTimeMillis() % 1000, System.currentTimeMillis() % 1000);

            //out.writeObject(v2);
        }
    }

    @Override
    public Object inplaceDeserialize(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.readInt(); // size // FIXME

        for (GameObject object : this) {
            //object.inplaceDeserialize(in);

            //long l = in.readLong();

            Vector2 pos = (Vector2) in.readObject();
        }

        return null;
    }

    public void update(float delta) {
        this.accumulator += delta;

        for (Iterator<GameObject> it = this.iterator(); it.hasNext();) {
            GameObject object = it.next();
            if (object.isDead()) {
                object.destroyBody(world);
                it.remove();
            }
        }

        foreach(this, object -> object.updateState(delta));

        while (this.accumulator >= WORLD_STEP) {
            this.step();
            this.accumulator -= WORLD_STEP;
        }

        // I don't think that interpolation is necessary.
        // It would be very hard and takes many resources.
    }

    public Body addObject(GameObject object, BodyDef bodyDef) {
        this.gameObjects.add(object);
        return this.world.createBody(bodyDef);
    }

    public FrictionJoint addFriction(Body body, float linearFriction, float angularFriction) {
        FrictionJointDef frictionDef = new FrictionJointDef();

        frictionDef.maxForce = linearFriction;
        frictionDef.maxTorque = angularFriction;

        frictionDef.initialize(body, ground, Vector2.Zero);

        return (FrictionJoint) this.world.createJoint(frictionDef);
    }


    private void step() {
        foreach(this, GameObject::updateForFirstStep);
        this.world.step(WORLD_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

        foreach(this, GameObject::updateForSecondStep);
        this.world.step(WORLD_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
    }
}
