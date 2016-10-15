package ru.spbau.blackout.entities;

import com.badlogic.gdx.Gdx;
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

import static ru.spbau.blackout.utils.Utils.fixTop;


public abstract class GameObject implements RenderableProvider {
    // physics:
    protected Body body;
    float height;

    // appearance:
    protected ModelInstance model;

    protected GameObject(Definition def, Model model, GameWorld gameWorld) {
        this.model = new ModelInstance(model);

        body = gameWorld.addObject(this, def);
        body.createFixture(def.fixtureDef);

        setPosition(def.getPosition().x, def.getPosition().y);
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        updateTransform();
        model.getRenderables(renderables, pool);
    }

    public void update(float delta) {
        // nothing
    }

    // FIXME: remove
    public ModelInstance getModel() {
        updateTransform();
        return model;
    }

    // Transform:

    public void setTransform(float x, float y, float angle) {
        body.setTransform(x, y, angle);
    }

    /**
     * Model instance model to the physic body.
     */
    private void updateTransform() {
        model.transform.setToRotationRad(Vector3.Z, body.getAngle());
        fixTop(model);
        Vector2 pos = body.getPosition();
        model.transform.setTranslation(pos.x, pos.y, height);
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

    public static abstract class Definition {
        public static final float DEFAULT_HEIGHT = 0;
        public static final float DEFAULT_ROTATION = 0;

        public static final float DEFAULT_DENSITY = 1f;
        public static final float DEFAULT_FRICTION = 0.4f;
        public static final float RESTITUTION = 0f;

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
            fixtureDef.restitution = RESTITUTION;

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
            rotation = direction.angleRad();
        }

        public void dispose() {
            fixtureDef.shape.dispose();
        }

        public Body addToWorld(World world) {
            return world.createBody(bodyDef);
        }

        public abstract GameObject makeInstance(Model model, GameWorld gameWorld);
        public abstract BodyDef.BodyType getBodyType();
//        public abstract float getDensity();
//        public abstract float getFriction();i
    }
}
