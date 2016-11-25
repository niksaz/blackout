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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ru.spbau.blackout.entities.GameObject;

public class GameWorld implements Iterable<GameObject>, Serializable {
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

    public void reset(GameWorld gameWorld) {
        final Iterator<GameObject> thisWorldIt = iterator();
        final Iterator<GameObject> otherWorldIt = iterator();
        while (thisWorldIt.hasNext() && otherWorldIt.hasNext()) {
            final GameObject thisObject = thisWorldIt.next();
            final GameObject otherObject = otherWorldIt.next();
            thisObject.reset(otherObject);
        }
    }

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

    public Body addObject(GameObject object, GameObject.Definition def) {
        gameObjects.add(object);
        return def.addToWorld(world);
    }

    public FrictionJoint addFriction(Body body, float linearFriction, float angularFriction) {
        FrictionJointDef frictionDef = new FrictionJointDef();

        frictionDef.maxForce = linearFriction;
        frictionDef.maxTorque = angularFriction;

        frictionDef.initialize(body, ground, Vector2.Zero);

        return (FrictionJoint) world.createJoint(frictionDef);
    }

    public Body addController(Body body) {
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

    private void step() {
        gameObjects.forEach(GameObject::updateForFirstStep);
        world.step(WORLD_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

        gameObjects.forEach(GameObject::updateForSecondStep);
        world.step(WORLD_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
    }

}
