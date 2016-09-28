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
public abstract class GameUnit {
    public static final float DEFAULT_HEIGHT = 5;

    public Vector2 position = new Vector2();

    public float height = DEFAULT_HEIGHT;

    // movement:
    protected Vector2 velocity = new Vector2();
    protected float speed = 1;

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

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public void makeInstance(Model model) {
        this.model = new ModelInstance(model, position.x , height, position.y);
        // FIXME: just for a test:
        animation = new AnimationController(this.model);
//        animation.setAnimation("Armature|Walk", -1);
    }

    public ModelInstance forRender(float delta) {
        update(delta);
        return model;
    }

    private void update(float delta) {
        preUpdate(delta);

        float newX = position.x + velocity.x * speed * delta;
        float newY = position.y + velocity.y * speed * delta;

        model.transform.setToTranslation(newX, height, newY);
        animation.update(delta);

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
}
