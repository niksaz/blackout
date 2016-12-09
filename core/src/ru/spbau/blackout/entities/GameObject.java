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
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.graphic_effects.GraphicEffect;
import ru.spbau.blackout.java8features.Optional;
import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.utils.InplaceSerializable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static ru.spbau.blackout.utils.Utils.fixTop;


public abstract class GameObject implements RenderableProvider, InplaceSerializable, Serializable {
    public static final float RESTITUTION = 0.5f;


    transient protected final Body body;
    private float height;

    /** Equals to Optional.empty() on a server or if the object is dead. */
    transient protected Optional<ModelInstance> model;
    transient public final Array<GraphicEffect> graphicEffects = new Array<>();

    private boolean dead = false;
    private final Vector3 chestPivotOffset;
    private final Vector3 overHeadPivotOffset;


    /**
     * Constructs defined object at the given position.
     */
    protected GameObject(Definition def, float x, float y) {
        this.model = def.model.map(ModelInstance::new);

        body = def.registerObject(this);
        body.setUserData(this);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = def.shapeCreator.create();
        fixtureDef.density = 1f;
        fixtureDef.friction = 0;
        fixtureDef.restitution = RESTITUTION;
        fixtureDef.isSensor = def.isSensor;
        body.createFixture(fixtureDef);
        fixtureDef.shape.dispose();

        this.chestPivotOffset = def.chestPivotOffset;
        this.overHeadPivotOffset = def.overHeadPivotOffset;
        this.setPosition(x, y);
        this.setMass(def.mass);
    }


    @Override
    public void inplaceSerialize(ObjectOutputStream out) throws IOException, ClassNotFoundException {
        out.writeObject(this);
        out.writeObject(this.getPosition());
        out.writeFloat(this.getRotation());
    }

    @Override
    public Object inplaceDeserialize(ObjectInputStream in) throws IOException, ClassNotFoundException {
        GameObject other = (GameObject) in.readObject();
        this.height = other.height;
        Vector2 position = (Vector2) in.readObject();
        float rotation = in.readFloat();
        this.setTransform(position, rotation);
        return other;
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        model.ifPresent(model -> {
            this.updateTransform();
            model.getRenderables(renderables, pool);
        });
    }


    /**
     * Update things not connected with physics. See <code>GameWorld</code> documentation.
     */
    public void updateState(float deltaTime) {
        for (GraphicEffect effect : this.graphicEffects) {
            effect.update(deltaTime);
        }
    }
    /** See <code>GameWorld</code> documentation. */
    public abstract void updateForFirstStep();
    /** See <code>GameWorld</code> documentation. */
    public abstract void updateForSecondStep();

    /**
     * Sets mass of the object. It mainly works just like expected:
     * the mass is higher, the harder to move object by applying external force.
     * One thing which can be unexpected is that velocity itself isn't connected with mass.
     */
    public void setMass(float newMass) {
        MassData massData = body.getMassData();

        float scaleFactor = newMass / massData.mass;
        massData.mass *= scaleFactor;
        massData.I *= scaleFactor;

        body.setMassData(massData);
    }

    /**
     * Kills the unit. It will be removed from the <code>GameWorld</code> and from the map after paying death animation.
     * Also calls <code>this.dispose()</code>.
     */
    public void kill() {
        // TODO: override
        // It will be handled in GameWorld::update. It's a bad idea to try to remove body
        // from GameWorld right here because this method can be called in process of updating physics.
        this.dead = true;
        // FIXME: play death animation
        this.model = Optional.empty();
        this.dispose();
    }

    public boolean isDead() { return this.dead; }

    /**
     * Disposes all non-shared resources (like graphicEffects).
     * Shared resources (like models) will be disposed by AssetManager.
     */
    public void dispose() {
        for (GraphicEffect effect : this.graphicEffects) {
            effect.remove();
        }
    }


    /**
     * This function is necessary for better encapsulation.
     * I don't want anyone to access object's <code>Body</code> directly as well as I don't want anyone
     * to access <code>World</code> (which is a part of <code>GameWorld</code> class) directly.
     */
    public void destroyBody(World world) {
        world.destroyBody(this.body);
    }

    // Transform:

    public void setTransform(float x, float y, float angle) {
        body.setTransform(x, y, angle);
    }

    public void setTransform(Vector2 position, float angle) {
        body.setTransform(position, angle);
    }

    protected void updateTransform() {
        model.ifPresent(model -> {
            model.transform.setToRotationRad(Vector3.Z, body.getAngle());
            fixTop(model);
            Vector2 pos = body.getPosition();
            model.transform.setTranslation(pos.x, pos.y, height);
        });
    }

    // Rotation

    /** Set rotation in radians. */
    public void setRotation(float angle) {
        Vector2 pos = getPosition();
        setTransform(pos.x, pos.y, angle);
    }

    /** Returns the current rotation in radians. */
    public float getRotation() {
        return body.getAngle();
    }

    /** Rotates object to the given direction. */
    public void setDirection(Vector2 direction) {
        setRotation(direction.angleRad());
    }


    public Vector2 getPosition() {
        return body.getPosition();
    }

    public void setPosition(Vector2 position) {
        setTransform(position, getRotation());
    }

    public void setPosition(float x, float y) {
        setTransform(x, y, getRotation());
    }

    /**
     * Returns new Vector3 (so, it's safe to change this vector without changing unit's position).
     */
    public Vector3 get3dPosition() {
        Vector2 pos = this.getPosition();
        return new Vector3(pos.x, pos.y, this.getHeight());
    }

    public void setHeight(float height) { this.height = height; }
    public final float getHeight() { return height; }

    public final float getMass() {
        return this.body.getMass();
    }


    public Vector3 getChestPivot() {
        return this.get3dPosition().add(chestPivotOffset);
    }

    public Vector3 getOverHeadPivot() {
        return this.get3dPosition().add(overHeadPivotOffset);
    }


    /**
     * Used to send via network a definition of an object to create.
     * Each kind of objects must have its own <code>Definition</code> subclass.
     *
     * <p>Life cycle:
     * <br>constructor (once)
     * <br>load (once)
     * <br>initialize (once)
     * <br>makeInstance (Any number of calls)
     */
    public static abstract class Definition implements Serializable {
        public static final float DEFAULT_HEIGHT = 0;
        public static final float DEFAULT_ROTATION = 0;

        public static final float DEFAULT_MASS = 70f;


        // physics
        public float rotation = DEFAULT_ROTATION;
        public float height = DEFAULT_HEIGHT;

        /** Mass of an object in kg */
        public float mass = DEFAULT_MASS;
        /**
         * As far as Shape itself isn't serializable,
         * supplier will be sent instead.
         */
        public Creator<Shape> shapeCreator;
        public final Vector2 position = new Vector2();

        /** The loaded model object. Initialized by <code>initialize</code> method. */
        private transient Optional<Model> model;

        /**
         * Path to the model for game objects. May be null. In this case objects will not have models.
         * Must be final due to possible problems if this variable changed between calls of `load` and `initialize`.
         */
        public final String modelPath;
        public final Vector3 chestPivotOffset = new Vector3();
        public final Vector3 overHeadPivotOffset = new Vector3();
        public boolean isSensor = false;


        /**
         * <code>modelPath</code> can be <code>null</code>.
         * In this case objects created from this definition will not have models.
         */
        public Definition(String modelPath, Creator<Shape> shapeCreator, float initialX, float initialY) {
            this.modelPath = modelPath;
            this.shapeCreator = shapeCreator;
            this.position.set(initialX, initialY);
        }

        /** Load necessary assets. */
        public void load() {
            GameContext context = BlackoutGame.get().context();
            if (context.hasIO() && this.modelPath != null) {
                context.getAssets().load(this.modelPath, Model.class);
            }
        }

        /** When assets are loaded. */
        public void doneLoading() {
            GameContext context = BlackoutGame.get().context();
            if (this.modelPath == null) {
                this.model = Optional.empty();
            } else {
                this.model = context.assets().map(assets -> assets.get(this.modelPath, Model.class));
            }
        }


        /** Create an object at the giving position. */
        public abstract GameObject makeInstance(float x, float y);
        /** Create an object at the giving position. */
        public GameObject makeInstance(Vector2 position) {
            return this.makeInstance(position.x, position.y);
        }
        /** Equal to <code>makeInstance(this.position)</code> */
        public GameObject makeInstance() {
            return this.makeInstance(this.position);
        }

        /** Rotates to the given direction. */
        public void setDirection(Vector2 direction) {
            this.rotation = direction.angleRad();
        }


        /**
         * Must be called from <code>GameObject</code> constructor to add this
         * <code>GameObject</code> to the world and make <code>Body</code> for it.
         */
        private Body registerObject(GameObject object) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.position.set(this.position);
            bodyDef.type = getBodyType();

            return BlackoutGame.get().context().gameWorld().addObject(object, bodyDef);
        }

        public abstract BodyDef.BodyType getBodyType();
    }
}
