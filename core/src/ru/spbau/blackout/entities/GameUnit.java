package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector2;

/**
 * Because the game is designed not as a game with many units,
 * but as a game with highly customized units, there is no class like `UnitType`.
 * So it contains all additional information like a path to its model.
 */
public class GameUnit {
    public static final float DEFAULT_HEIGHT = 5;

    private Vector2 position = new Vector2();
    public float height = DEFAULT_HEIGHT;

    // movement:
    protected Vector2 velocity = new Vector2();
    protected Vector2 selfVelocity = new Vector2();
    protected float speed = 10;

    // appearance
    protected ModelInstance model;
    protected AnimationController animation;

    protected String modelPath;

    public GameUnit(String modelPath, float initialX, float initialY) {
        this.modelPath = modelPath;
        this.position = new Vector2(initialX, initialY);
    }

    public GameUnit(String modelPath, Vector2 initialPosition) {
        this(modelPath, initialPosition.x, initialPosition.y);
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

    public void setSelfVelocity(Vector2 selfVelocity) {
        this.selfVelocity = selfVelocity;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
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
        this.model = new ModelInstance(model, position.x, position.y, height);
        animation = new AnimationController(this.model);

        onInstance();
    }

    public ModelInstance getModelInstance() {
        return model;
    }

    public final void update(float delta) {
        preUpdate(delta);

        float newX = position.x + (velocity.x + selfVelocity.x * speed) * delta;
        float newY = position.y + (velocity.y + selfVelocity.y * speed) * delta;

        position.set(newX, newY);

        animation.update(delta);
        model.transform.setToTranslation(position.x, position.y, height);

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
