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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.graphiceffects.GraphicEffect;
import ru.spbau.blackout.java8features.Optional;
import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.utils.HasState;

import static ru.spbau.blackout.utils.Utils.fixTop;


public abstract class GameObject implements RenderableProvider, HasState {
    public static final float RESTITUTION = 0.5f;


    protected final Body body;
    private float height;

    /** Equals to Optional.empty() on a server or if the object is dead. */
    protected Optional<ModelInstance> model;
    public final Array<GraphicEffect> graphicEffects = new Array<>();

    private boolean dead = false;
    private final GameObject.Definition def;
    private final long uid;


    /**
     * Constructs defined object at the given position.
     */
    protected GameObject(Definition def, long uid, float x, float y) {
        this.def = def;
        this.uid = uid;

        model = def.model.map(ModelInstance::new);

        body = def.registerObject(def.context, this);
        body.setUserData(this);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = def.shapeCreator.create();
        fixtureDef.density = 1f;
        fixtureDef.friction = 0;
        fixtureDef.restitution = RESTITUTION;
        fixtureDef.isSensor = def.isSensor;
        body.createFixture(fixtureDef);
        fixtureDef.shape.dispose();

        setPosition(x, y);
        setMass(def.mass);
    }


    @Override
    public void getState(ObjectOutputStream out) throws IOException, ClassNotFoundException {
        out.writeFloat(height);
        out.writeObject(getPosition());
        out.writeFloat(getRotation());
    }

    @Override
    public void setState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        height = in.readFloat();
        Vector2 position = (Vector2) in.readObject();
        float rotation = in.readFloat();
        setTransform(position, rotation);
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        model.ifPresent(model -> {
            updateTransform();
            model.getRenderables(renderables, pool);
        });
    }


    public void updateGraphics(float delta) {
        for (GraphicEffect effect : graphicEffects) {
            effect.update(delta);
        }
    }

    /**
     * Update things which are not connected with physics and don't require fixed step.
     * See <code>GameWorld</code> documentation.
     */
    public void updateState(float delta) {

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
     * Kills the unit. It will be removed from the <code>GameWorld</code> and from the mapPath after paying death animation.
     * Also calls <code>dispose()</code>.
     */
    public void kill() {
        // TODO: override
        // It will be handled in GameWorld::update. It's a bad idea to try to remove body
        // from GameWorld right here because this method can be called in process of updating physics.
        dead = true;
        // FIXME: play death animation
        model = Optional.empty();
        dispose();
    }

    public boolean isDead() { return dead; }

    /**
     * Disposes all non-shared resources (like graphicEffects).
     * Shared resources (like models) will be disposed by AssetManager.
     */
    public void dispose() {
        for (GraphicEffect effect : graphicEffects) {
            effect.remove();
        }
    }


    /**
     * This function is necessary for better encapsulation.
     * I don't want anyone to access object's <code>Body</code> directly as well as I don't want anyone
     * to access <code>World</code> (which is a part of <code>GameWorld</code> class) directly.
     */
    public void destroyBody(World world) {
        world.destroyBody(body);
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

    public long getUid() { return uid; }

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
        Vector2 pos = getPosition();
        return new Vector3(pos.x, pos.y, getHeight());
    }

    public void setHeight(float height) { this.height = height; }
    public final float getHeight() { return height; }

    public final float getMass() {
        return body.getMass();
    }


    public Vector3 getChestPivot() {
        return get3dPosition().add(def.chestPivotOffset);
    }

    public Vector3 getOverHeadPivot() {
        return get3dPosition().add(def.overHeadPivotOffset);
    }

    public Definition getDef() {
        return def;
    }


    /**
     * Used to send via network a definition of an object to create.
     * Each kind of objects must have its own <code>Definition</code> subclass.
     *
     * <p>Life cycle:
     * <br>constructor (once)
     * <br>load (once)
     * <br>initializeGameWorld (once)
     * <br>makeInstance (Any number of calls)
     */
    public static abstract class Definition implements Serializable {

        private static final long serialVersionUID = 1000000000L;

        public static final float DEFAULT_HEIGHT = 0;

        public static final float DEFAULT_MASS = 70f;


        protected static long s_lastUid = 0;

        // physics
        public float height = DEFAULT_HEIGHT;

        /** Mass of an object in kg */
        public float mass = DEFAULT_MASS;
        /**
         * As far as Shape itself isn't serializable,
         * supplier will be sent instead.
         */
        public Creator<Shape> shapeCreator;

        /** The loaded model object. Initialized by <code>initializeGameWorld</code> method. */
        private transient Optional<Model> model;
        protected transient GameContext context;

        /**
         * Path to the model for game objects. May be null. In this case objects will not have models.
         * Must be final due to possible problems if this variable changed between calls of `load` and `initializeGameWorld`.
         */
        public final String modelPath;
        public final Vector3 chestPivotOffset = new Vector3();
        public final Vector3 overHeadPivotOffset = new Vector3();
        public boolean isSensor = false;

        private int defNumber;


        /**
         * <code>modelPath</code> can be <code>null</code>.
         * In this case objects created from this definition will not have models.
         */
        public Definition(String modelPath, Creator<Shape> shapeCreator) {
            this.modelPath = modelPath;
            this.shapeCreator = shapeCreator;
        }

        /** Load necessary assets. */
        public void load(GameContext context) {
            if (!context.hasUI()) {
                throw new IllegalArgumentException("Don't use `load` method on server.");
            }

            this.context = context;
            if (modelPath != null) {
                context.getAssets().load(modelPath, Model.class);
            }
        }

        public void initializeWithoutUi(GameContext context) {
            this.context = context;
            model = Optional.empty();
        }

        /** When assets are loaded. */
        public void doneLoading() {
            if (!context.hasUI()) {
                throw new IllegalArgumentException("Don't use `doneLoading` method on server.");
            }

            if (modelPath == null) {
                model = Optional.empty();
            } else {
                model = Optional.of(context.getAssets().get(modelPath, Model.class));
            }
        }


        /** Create an object at the giving position. */
        public abstract GameObject makeInstance(long uid, float x, float y);
        /** Create an object at the giving position. */
        public GameObject makeInstance(long uid, Vector2 position) {
            return makeInstance(uid, position.x, position.y);
        }
        /** Create an object at (0, 0). */
        public GameObject makeInstance(long uid) {
            return makeInstance(uid, 0, 0);
        }

        /**
         * Create an object at the giving position.
         */
        public GameObject makeInstanceWithNextUid(float x, float y) {
            return makeInstance(getNextUid(), x, y);
        }
        /** Create an object at the giving position. */
        public GameObject makeInstanceWithNextUid(Vector2 position) {
            return makeInstanceWithNextUid(position.x, position.y);
        }

        /**
         * Must be called from <code>GameObject</code> constructor to add this
         * <code>GameObject</code> to the world and make <code>Body</code> for it.
         */
        private Body registerObject(GameContext context, GameObject object) {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = getBodyType();

            return context.gameWorld().addObject(object, bodyDef);
        }

        public abstract BodyDef.BodyType getBodyType();

        public static long getNextUid() {
            s_lastUid += 1;
            return s_lastUid;
        }

        public int getDefNumber() { return defNumber; }

        public void setDefNumber(int defNumber) { this.defNumber = defNumber; }
    }
}
