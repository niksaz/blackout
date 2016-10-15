package ru.spbau.blackout;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.FrictionJoint;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

import ru.spbau.blackout.entities.GameObject;

public class GameWorld implements Iterable<GameObject> {
    public static final float WORLD_STEP = 1 / 58f;
    public static final int VELOCITY_ITERATIONS = 2;
    public static final int POSITION_ITERATIONS = 2;

    private final Array<GameObject> gameObjects = new Array<>();
    private final World world;
    private float accumulator = 0;
    private Body ground;

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

    public FrictionJoint addFriction(Body body, float linearFriction, float angularFriction) {
        FrictionJointDef frictionDef = new FrictionJointDef();

        frictionDef.localAnchorA.set(0,0);
        frictionDef.localAnchorB.set(0,0);

        frictionDef.maxForce = linearFriction;
        frictionDef.maxTorque = angularFriction;

        frictionDef.bodyA = body;
        frictionDef.bodyB = ground;

        return (FrictionJoint) world.createJoint(frictionDef);
    }

    private void step() {
        for (GameObject object : gameObjects) {
            object.update(WORLD_STEP);
        }

        world.step(WORLD_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
    }
}
