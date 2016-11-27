package ru.spbau.blackout;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.FrictionJoint;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.utils.InplaceSerializable;

public class GameWorld implements Iterable<GameObject>, InplaceSerializable {
    public static final float WORLD_STEP = 1 / 58f;
    public static final int VELOCITY_ITERATIONS = 1;
    public static final int POSITION_ITERATIONS = 2;

    private final List<GameObject> gameObjects = new ArrayList<>();
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
            //System.out.println("some object des");
            //object.inplaceSerialize(out);

            Vector2 sending = object.getPosition();
            //Vector2 sending = new Vector2(v2.x, v2.y);
            out.writeFloat(sending.x);
            out.writeFloat(sending.y);
            if (object instanceof GameUnit) {
                GameUnit unit = (GameUnit) object;
                sending = unit.getSelfVelocity();
                out.writeFloat(sending.x);
                out.writeFloat(sending.y);
            }
            //long curTime = System.currentTimeMillis();
            //out.writeLong(curTime);

//            Vector2 ob = object.getPosition();
//            Vector2 c = new Vector2(ob.x, ob.y);
//            out.writeObject(c);
//            System.out.println("sent time " + c);
            //Vector2 v2 = ;//new Vector2(System.currentTimeMillis() % 1000, System.currentTimeMillis() % 1000);

            //out.writeObject(v2);
        }
    }

    @Override
    public synchronized Object inplaceDeserialize(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.readInt(); // size // FIXME

        for (GameObject object : this) {
            //object.inplaceDeserialize(in);

            float x = in.readFloat();
            float y = in.readFloat();
            object.setPosition(x, y);
            if (object instanceof GameUnit) {
                GameUnit unit = (GameUnit) object;
                x = in.readFloat();
                y = in.readFloat();
                unit.setSelfVelocity(new Vector2(x, y));
            }

            //Vector2 v2 = (Vector2) in.readObject();
            //Gdx.app.log("ANDROID", "got position: " + x + " " + y);

            //long l = in.readLong();

            //Vector2 pos = (Vector2) in.readObject();
            //Gdx.app.log("ANDROID", "got time " + pos);
        }

        return null;
    }

    public void reset(GameWorld otherWorld) {

    }

    public synchronized void update(float delta) {
        System.out.println(delta);
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

    public synchronized Body addObject(GameObject object, GameObject.Definition def) {
        gameObjects.add(object);
        return def.addToWorld(world);
    }

    public synchronized FrictionJoint addFriction(Body body, float linearFriction, float angularFriction) {
        FrictionJointDef frictionDef = new FrictionJointDef();

        frictionDef.maxForce = linearFriction;
        frictionDef.maxTorque = angularFriction;

        frictionDef.initialize(body, ground, Vector2.Zero);

        return (FrictionJoint) world.createJoint(frictionDef);
    }

    public synchronized Body addController(Body body) {
        // Create a controller's body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(body.getPosition().x, body.getPosition().y);
        Body controller = world.createBody(bodyDef);

        // Glue it with the target body
        WeldJointDef jointDef = new WeldJointDef();
        jointDef.initialize(body, controller, Vector2.Zero);
        world.createJoint(jointDef);

        return controller;
    }

    private synchronized void step() {
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

        //for (GameObject object : this) {
        //    System.out.println("Object position: " + object.getPosition());
        //}
    }
}
