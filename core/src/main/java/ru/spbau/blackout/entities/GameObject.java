package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
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

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.effects.Effect;
import ru.spbau.blackout.effects.GraphicEffect;
import ru.spbau.blackout.effects.PhysicEffect;
import ru.spbau.blackout.serializationutils.EfficientInputStream;
import ru.spbau.blackout.serializationutils.EfficientOutputStream;
import ru.spbau.blackout.specialeffects.ParticleSpecialEffect;
import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.serializationutils.HasState;
import ru.spbau.blackout.utils.Particles;
import ru.spbau.blackout.utils.Uid;
import ru.spbau.blackout.worlds.ServerGameWorld;

import static ru.spbau.blackout.java8features.Functional.foreach;
import static ru.spbau.blackout.utils.Utils.fixTop;


public abstract class GameObject implements RenderableProvider, HasState {

    public static final float RESTITUTION = 0.5f;

    protected final Body body;
    private float height;

    /** Equals to Optional.empty() on a server or if the object is dead. */
    @Nullable protected ModelInstance modelInstance;
    private final Set<PhysicEffect> physicEffects = new HashSet<>();
    private final Set<GraphicEffect> graphicEffects = new HashSet<>();

    private boolean dead = false;
    private final GameObject.Definition def;
    private final Uid uid;
    /** Is used to rotate modelInstance inside <code>updateModelPosition</code> method; */
    private float lastRotation = 0;

    /**
     * Constructs defined object at the given touchPos.
     */
    protected GameObject(Definition def, Uid uid, float x, float y) {
        this.def = def;
        this.uid = uid;

        if (def.model != null) {
            modelInstance = new ModelInstance(def.model);
            fixTop(modelInstance);
        }

        body = def.registerObject(def.context, this);
        body.setUserData(this);

        if (def.shapeCreator != null) {
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = def.shapeCreator.create();
            fixtureDef.density = 1f;
            fixtureDef.friction = 0;
            fixtureDef.restitution = RESTITUTION;
            fixtureDef.isSensor = def.isSensor;
            body.createFixture(fixtureDef);
            fixtureDef.shape.dispose();
        }

        setPosition(x, y);
        setMass(def.mass);
    }

    @Nullable
    public final ModelInstance getModelInstance() {
        return modelInstance;
    }

    @Override
    public void getState(EfficientOutputStream out) throws IOException {
        out.writeFloat(height);
        out.writeVector2(getPosition());
        out.writeFloat(getRotation());
    }

    @Override
    public void setState(EfficientInputStream in) throws IOException {
        height = in.readFloat();
        Vector2 position = in.readVector2();
        float rotation = in.readFloat();
        setTransform(position, rotation);
    }

    @Override
    public final void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        if (modelInstance != null) {
            updateModelPosition();
            modelInstance.getRenderables(renderables, pool);
        }
    }


    public void updateGraphics(float delta) {
        for (Effect effect : getGraphicEffects()) {
            effect.update(delta);
        }
    }

    public void updateState(float delta) {
        for (Effect effect : getPhysicEffects()) {
            effect.update(delta);
        }
    }

    /** See <code>GameWorld</code> documentation. */
    public void updateBeforeFirstStep() {}
    /** See <code>GameWorld</code> documentation. */
    public void updateBeforeSecondStep() {}

    /**
     * Sets mass of the object. It mainly works just like expected:
     * the mass is higher, the harder to move object by applying external force.
     * One thing which can be unexpected is that velocity itself isn't connected with mass.
     */
    public final void setMass(float newMass) {
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
        // It will be handled in GameWorld::updatePhysics. It's a bad idea to try to remove body
        // from GameWorld right here because this method can be called in process of updating physics.
        dead = true;
        modelInstance = null;
        if (getDef().deathEffect != null) {
            ParticleSpecialEffect.create(getDef().getContext(), getDef().deathEffect, getChestPivot());
        }
        dispose();
    }

    public final boolean isDead() { return dead; }

    /**
     * Disposes all non-shared resources (like graphicEffects).
     * Shared resources (like models) will be disposed by AssetManager.
     */
    public void dispose() {
        foreach(getGraphicEffects(), Effect::dispose);
        foreach(getPhysicEffects(), Effect::dispose);
    }

    /**
     * This function is necessary for better encapsulation.
     * I don't want anyone to access object's <code>Body</code> directly as well as I don't want anyone
     * to access <code>World</code> (which is a part of <code>GameWorld</code> class) directly.
     */
    public final void destroyBody(World world) {
        world.destroyBody(body);
    }

    // Transform:

    public final void setTransform(float x, float y, float angle) {
        body.setTransform(x, y, angle);
    }

    public final void setTransform(Vector2 position, float angle) {
        body.setTransform(position, angle);
    }

    private void updateModelPosition() {
        if (modelInstance != null) {
            float newRotation = getRotation();
            modelInstance.transform.rotateRad(Vector3.Y, newRotation - lastRotation);
            lastRotation = newRotation;
            Vector2 pos = getPosition();
            modelInstance.transform.setTranslation(pos.x, pos.y, height);
            modelInstance.calculateTransforms();
        }
    }

    // Rotation

    /** Set rotation in radians. */
    public final void setRotation(float angle) {
        Vector2 pos = getPosition();
        setTransform(pos.x, pos.y, angle);
    }

    public final void rotate(float angle) {
        setRotation(getRotation() + angle);
    }

    /** Returns the current rotation in radians. */
    public final float getRotation() {
        return body.getAngle();
    }

    /** Rotates object to the given direction. */
    public final void setDirection(Vector2 direction) {
        setRotation(direction.angleRad());
    }

    public final Uid getUid() { return uid; }

    public final Vector2 getPosition() {
        return body.getPosition();
    }

    public final void setPosition(Vector2 position) {
        setTransform(position, getRotation());
    }

    public final void setPosition(float x, float y) {
        setTransform(x, y, getRotation());
    }

    /**
     * Returns new Vector3 (so, it's safe to change this vector without changing unit's touchPos).
     */
    public final Vector3 get3dPosition() {
        Vector2 pos = getPosition();
        return new Vector3(pos.x, pos.y, getHeight());
    }

    public final void setHeight(float height) { this.height = height; }
    public final float getHeight() { return height; }

    public final float getMass() {
        return body.getMass();
    }


    public final Vector3 getChestPivot() {
        return get3dPosition().add(def.chestPivotOffset);
    }

    public final Vector3 getOverHeadPivot() {
        return get3dPosition().add(def.overHeadPivotOffset);
    }

    public final Definition getDef() {
        return def;
    }

    public final Set<GraphicEffect> getGraphicEffects() {
        return graphicEffects;
    }

    public final Set<PhysicEffect> getPhysicEffects() {
        return physicEffects;
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
        private static final float DEFAULT_CHEST_HEIGHT = 1.5f;


        // physics
        public float height = DEFAULT_HEIGHT;

        /** Mass of an object in kg */
        public float mass = DEFAULT_MASS;

        /**
         * As far as Shape itself isn't serializable,
         * supplier will be sent instead.
         */
        @Nullable
        private final Creator<Shape> shapeCreator;

        /** The loaded modelInstance object. Initialized by <code>initializeGameWorld</code> method. */
        @Nullable
        private transient Model model;
        @Nullable
        private transient ParticleEffect deathEffect;
        @Nullable
        private final String deathEffectPath;
        private transient GameContext context;

        /**
         * Path to the modelInstance for game objects. May be null. In this case objects will not have models.
         * Must be final due to possible problems if this variable changed between calls of `load` and `initializeGameWorld`.
         */
        public final String modelPath;
        public final Vector3 chestPivotOffset = new Vector3(0, 0, DEFAULT_CHEST_HEIGHT);
        public final Vector3 overHeadPivotOffset = new Vector3();
        public boolean isSensor = false;
        private int defNumber;


        /**
         * <code>modelPath</code> can be <code>null</code>.
         * In this case objects created from this definition will not have models.
         */
        public Definition(@Nullable String modelPath, @Nullable Creator<Shape> shapeCreator,
                          @Nullable String deathEffectPath) {
            this.modelPath = modelPath;
            this.shapeCreator = shapeCreator;
            this.deathEffectPath = deathEffectPath;
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
            if (deathEffectPath != null) {
                Particles.load(context, deathEffectPath);
            }
        }

        public final GameContext getContext() {
            return context;
        }

        public void initializeWithoutUi(GameContext context) {
            this.context = context;
        }

        /** When assets are loaded. */
        public void doneLoading() {
            if (!context.hasUI()) {
                throw new IllegalArgumentException("Don't use `doneLoading` method on server.");
            }

            if (modelPath != null) {
                model = context.getAssets().get(modelPath, Model.class);
            }
            if (deathEffectPath != null) {
                deathEffect = Particles.getOriginal(getContext(), deathEffectPath);
            }
        }


        /** Create an object at the giving touchPos. */
        public abstract GameObject makeInstance(Uid uid, float x, float y);
        /** Create an object at the giving touchPos. */
        public final GameObject makeInstance(Uid uid, Vector2 position) {
            return makeInstance(uid, position.x, position.y);
        }
        /** Create an object at (0, 0). */
        public final GameObject makeInstance(Uid uid) {
            return makeInstance(uid, 0, 0);
        }

        /**
         * Creates new instance of the unit with nextUid.
         * Must be called only on <code>ServerGameWorld</code>
         */
        public final GameObject makeInstanceWithNextUid(float x, float y) {
            return makeInstance(((ServerGameWorld) context.gameWorld()).uidGenerator.next(), x, y);
        }

        /** Create an object at the giving touchPos. */
        public final GameObject makeInstanceWithNextUid(Vector2 position) {
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

        public void dispose() {
        }

        public abstract BodyDef.BodyType getBodyType();

        public final int getDefNumber() {
            return defNumber;
        }

        public final void setDefNumber(int defNumber) {
            this.defNumber = defNumber;
        }
    }
}
