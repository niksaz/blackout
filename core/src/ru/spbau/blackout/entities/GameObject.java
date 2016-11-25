package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import ru.spbau.blackout.GameWorld;
import ru.spbau.blackout.utils.Creator;

import java.io.IOException;
import java.io.Serializable;

import static ru.spbau.blackout.utils.Utils.fixTop;


public abstract class GameObject implements RenderableProvider, Serializable {
    // physics:
    transient protected Body body;
    private float height;

    // appearance:
    transient protected ModelInstance model;

    protected GameObject(Definition def, Model model, GameWorld gameWorld) {
        this.model = model == null ? null : new ModelInstance(model);

        body = gameWorld.addObject(this, def);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = def.getShapeCreator().create();
        fixtureDef.density = def.getDensity();
        fixtureDef.friction = 0;
        fixtureDef.restitution = 0;
        body.createFixture(fixtureDef);
        fixtureDef.shape.dispose();

        setPosition(def.getPosition().x, def.getPosition().y);
    }

    /**
     * Serialization overriding. We have to handle that Body isn't serializable
     * (we need its position and rotation)
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.getPosition());
        out.writeFloat(this.getRotation());
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        Vector2 position = (Vector2) in.readObject();
        float rotation = in.readFloat();
        this.setTransform(position, rotation);
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        updateTransform();
        model.getRenderables(renderables, pool);
    }

    /**
     * Update things not connected with physics.
     */
    public void updateState(float delta) {}

    public void updateForFirstStep() {}
    public void updateForSecondStep() {}

    // Transform:

    public void setTransform(float x, float y, float angle) {
        body.setTransform(x, y, angle);
    }

    public void setTransform(Vector2 position, float angle) {
        body.setTransform(position, angle);
    }

    /**
     * Model instance model to the physic body.
     */
    protected void updateTransform() {
        model.transform.setToRotationRad(Vector3.Z, body.getAngle());
        fixTop(model);
        Vector2 pos = body.getPosition();
        model.transform.setTranslation(pos.x, pos.y, height);
//        model.calculateTransforms();
    }

    // Rotation

    /**
     * Set rotation in radians.
     */
    public void setRotation(float angle) {
        Vector2 pos = getPosition();
        setTransform(pos.x, pos.y, angle);
    }

    /**
     * Rotates object to the given direction.
     */
    public void setDirection(Vector2 direction) {
        setRotation(direction.angleRad());
    }

    /**
     *  The current rotation in radians.
     */
    public float getRotation() {
        return body.getAngle();
    }


    // Position:

    /**
     *
     */
    public Vector2 getPosition() {
        return body.getPosition();
    }

    public void setPosition(float x, float y) {
        setTransform(x, y, getRotation());
    }

    // Height:

    public void setHeight(float height) {
        this.height = height;
    }

    public final float getHeight() {
        return height;
    }

    public void reset(GameObject other) {
        this.height = other.height;
    }



    public static abstract class Definition implements Serializable {
        public static final float DEFAULT_HEIGHT = 0;
        public static final float DEFAULT_ROTATION = 0;

        public static final float DEFAULT_DENSITY = 1f;

        // physics
        public float rotation = DEFAULT_ROTATION;
        public float height = DEFAULT_HEIGHT;

        private float density = DEFAULT_DENSITY;
        /**
         * As far as Shape itself isn't serializable,
         * supplier will be sent instead.
         */
        private Creator<Shape> shapeCreator;
        private final Vector2 position = new Vector2();


        // appearance:
        public String modelPath;

        /**
         * ShapeCreator must be serializable.
         */
        public Definition(String modelPath, Creator<Shape> shapeCreator,
                          float initialX, float initialY)
        {
            this.modelPath = modelPath;
            this.shapeCreator = shapeCreator;
            this.position.set(initialX, initialY);
        }

        public void setDensity(float density) {
            this.density = density;
        }

        public void setShapeCreator(Creator<Shape> shapeCreator) {
            this.shapeCreator = shapeCreator;
        }

        public float getDensity() {
            return this.density;
        }

        public Creator<Shape> getShapeCreator() {
            return this.shapeCreator;
        }

        public void setHeight(float height) {
            this.height = height;
        }

        public float getHeight() {
            return this.height;
        }

        public Vector2 getPosition() {
            return this.position;
        }

        public void setPosition(float x, float y) {
            this.position.set(x, y);
        }

        /**
         * Rotates object to the given direction.
         */
        public void setDirection(Vector2 direction) {
            this.rotation = direction.angleRad();
        }

        public Body addToWorld(World world) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.position.set(this.position);
            bodyDef.type = getBodyType();
            return world.createBody(bodyDef);
        }

        public abstract GameObject makeInstance(Model model, GameWorld gameWorld);
        public abstract BodyDef.BodyType getBodyType();
//        public abstract float getDensity();
//        public abstract float getFriction();i
    }
}
