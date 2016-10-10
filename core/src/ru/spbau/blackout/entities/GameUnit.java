package ru.spbau.blackout.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ru.spbau.blackout.Utils;
/**
 * Because the game is designed not as a game with many units,
 * but as a game with highly customized units, there is no class like `UnitType`.
 * So it contains all additional information like a path to its model.
 */
public class GameUnit {
    // TODO: find or make an acceptable model

    public static class Animations {
        public static final String WALK = "Armature|Walk";
        public static final String STAY = "Armature|Hit"; // TODO: normal animation
        public static final float WALK_SPEED_FACTOR = 2f;
    }

    public static final float DEFAULT_HEIGHT = 5;

    final private Vector2 position = new Vector2();
    public float height = DEFAULT_HEIGHT;

    // movement:
    final protected Vector2 velocity = new Vector2();
    /**
     *
     */
    final protected Vector2 selfVelocity = new Vector2();
    protected float speed = 7f;

    // appearance
    protected ModelInstance model;
    protected AnimationController animation;
    protected float animationSpeed = 1f;

    protected String modelPath;

    public GameUnit(String modelPath, float initialX, float initialY) {
        this.modelPath = modelPath;
        this.position.set(initialX, initialY);
    }

    public GameUnit(String modelPath, Vector2 initialPosition) {
        this(modelPath, initialPosition.x, initialPosition.y);
    }

    public GameUnit(String modelPath) {
        this(modelPath, 0, 0);
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Vector2 getSelfVelocity() {
        return selfVelocity;
    }

    /**
     * Rotation in radians.
     */
    public void setRotation(float rad) {
        // FIXME: should use setRotation method instead. But doesn't exist
        model.transform.setToRotationRad(Vector3.Z, rad);
    }

    /**
     * Rotates unit to the given direction.
     */
    public void setDirection(Vector2 direction) {
        setRotation(direction.angleRad());
    }

    public void setSelfVelocity(final Vector2 vel) {
        // avoid excessive allocation
        // Vector2 old = new Vector2(selfVelocity);
        final float oldX = selfVelocity.x;
        final float oldY = selfVelocity.y;

        selfVelocity.set(vel.x, vel.y);
        if (Utils.isZeroVec(selfVelocity)) {
            // on stop walking
            if (!Utils.isZeroVec(oldX, oldY)) {
                animation.setAnimation(Animations.STAY, -1);
                animationSpeed = 1f;
            }
        } else {
            // on start walking
            if (Utils.isZeroVec(oldX, oldY)) {
                animation.setAnimation(Animations.WALK, -1);
            }

            animationSpeed = selfVelocity.len() * Animations.WALK_SPEED_FACTOR;
            setDirection(selfVelocity);
        }
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity.set(velocity);
    }

    public void addVelocity(Vector2 velocity) {
        this.velocity.x += velocity.x;
        this.velocity.y += velocity.y;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position.set(x, y);
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public final void makeInstance(Model model) {
        this.model = new ModelInstance(model, getPosition().x, getPosition().y, height);
        animation = new AnimationController(this.model);
        animation.setAnimation(Animations.STAY, -1);

        onInstance();
    }

    public ModelInstance getModelInstance() {
        return model;
    }

    public final void update(float delta) {
        preUpdate(delta);

        float newX = getPosition().x + (getVelocity().x + getSelfVelocity().x * speed) * delta;
        float newY = getPosition().y + (getVelocity().y + getSelfVelocity().y * speed) * delta;

        setPosition(newX, newY);
        model.transform.setTranslation(getPosition().x, getPosition().y, getHeight());

        animation.update(delta * animationSpeed);

        postUpdate(delta);
    }

    /**
     * for overloading
     */
    protected void preUpdate(float delta) {}

    /**
     * for overloading
     */
    protected void postUpdate(float delta) {}

    /**
     * for overloading
     */
    protected void onInstance() {}
}
