package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

import ru.spbau.blackout.Physics;
import ru.spbau.blackout.Utils;


public abstract class GameObject {
    // physics:
    protected Body body;
    float height;

    // appearance:
    protected ModelInstance model;

    protected GameObject(Definition def, Model model, Physics physics) {
        this.model = new ModelInstance(model, def.getPosition().x, def.height, def.getPosition().y);
        body = physics.getWorld().createBody(def.bodyDef);
    }

    public final float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * Rotation in radians.
     */
    public void setRotation(float rad) {
        model.transform.setToRotationRad(Vector3.Y, rad);
    }

    /**
     * Rotates object to the given direction.
     */
    public void setDirection(Vector2 direction) {
        setRotation(Utils.angleVec(direction));
    }

    /*public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }*/

    public void update(float delta) {
        // nothing
    }

    public static abstract class Definition {
        public static final float DEFAULT_HEIGHT = 0;
        public static final float DEFAULT_ROTATION = 0;

        // physics
        public float rotation = DEFAULT_ROTATION;
        public float height = DEFAULT_HEIGHT;
        // final because it contains some invariants
        public final BodyDef bodyDef = new BodyDef();

        // appearance:
        public String modelPath;

        public Definition(String modelPath, float initialX, float initialY) {
            this.modelPath = modelPath;
            bodyDef.position.set(initialX, initialY);
        }

        public final float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            this.height = height;
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
    }
}
