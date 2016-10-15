package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.sun.org.apache.xpath.internal.operations.Mod;

import ru.spbau.blackout.Physics;
import ru.spbau.blackout.Utils;
import sun.security.provider.SHA;


public abstract class GameObject {
    // physics:
    protected Body body;
    float height;
    private final Vector2 position = new Vector2();

    // appearance:
    protected ModelInstance model;


    protected GameObject(Definition def, Model model, Physics physics) {
        this.model = new ModelInstance(model);
        body = physics.getWorld().createBody(def.bodyDef);
        setPosition(def.getPosition().x, def.getPosition().y);
    }

    public void update(float delta) {
        // nothing
    }


    // Rotation

    /**
     * Set rotation in radians.
     */
    public void setRotation(float rad) {
        model.transform.setToRotationRad(Vector3.Y, rad);
        body.getTransform().setRotation(rad);
    }

    /**
     * Rotates object to the given direction.
     */
    public void setDirection(Vector2 direction) {
        setRotation(Utils.angleVec(direction));
    }

    /**
     *  The current rotation in radians.
     */
    public float getRotation() {
        return body.getTransform().getRotation();
    }


    // Position:

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
        body.getTransform().setPosition(position);
        model.transform.setTranslation(body.getPosition().x, height, body.getPosition().y);
    }


    // ModelInstance

    public ModelInstance getModelInstance() {
        return model;
    }


    // Height:

    public void setHeight(float height) {
        this.height = height;
    }

    public final float getHeight() {
        return height;
    }

    public static abstract class Definition {
        public static final float DEFAULT_HEIGHT = 0;
        public static final float DEFAULT_ROTATION = 0;

        public static final float DEFAULT_DENSITY = 0.5f;
        public static final float DEFAULT_FRICTION = 0.4f;

        // physics
        public float rotation = DEFAULT_ROTATION;
        public float height = DEFAULT_HEIGHT;
        private final FixtureDef fixtureDef = new FixtureDef();
        private final BodyDef bodyDef = new BodyDef();

        // appearance:
        public String modelPath;

        public Definition(String modelPath, Shape shape, float initialX, float initialY) {
            this.modelPath = modelPath;

            // setup fixture
            fixtureDef.shape = shape;
            fixtureDef.density = DEFAULT_DENSITY;
            fixtureDef.friction = DEFAULT_FRICTION;
            fixtureDef.restitution = 0;

            // setup body
            bodyDef.position.set(initialX, initialY);
            bodyDef.type = getBodyType();
        }

        public void setDensity(float density) {
            fixtureDef.density = density;
        }

        public void setFriction(float friction) {
            fixtureDef.friction = friction;
        }

        public float getDensity() {
            return fixtureDef.density;
        }

        public float getFriction() {
            return fixtureDef.friction;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        public float getHeight() {
            return height;
        }

        public Vector2 getPosition() {
            return bodyDef.position;
        }

        public void setPosition(float x, float y) {
            bodyDef.position.set(x, y);
        }

        /**
         * Rotates object to the given direction.
         */
        public void setDirection(Vector2 direction) {
            rotation = Utils.angleVec(direction);
        }

        public abstract GameObject makeInstance(Model model, Physics physics);

        public abstract BodyDef.BodyType getBodyType();
//        public abstract float getDensity();
//        public abstract float getFriction();
    }
}
