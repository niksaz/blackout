package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ru.spbau.blackout.Utils;

/**
 * Because the game is designed not as a game with many objects,
 * but as a game with highly customized objects, there is no class like `UnitType`.
 * So it contains all additional information like a path to its model.
 */
public abstract class GameObject {
    public static final float DEFAULT_HEIGHT = 0;

    // position:
    final private Vector2 position = new Vector2();
    float height = DEFAULT_HEIGHT;

    // appearance:
    protected ModelInstance model;
    private String modelPath;

    public GameObject(String modelPath, float initialX, float initialY) {
        this.modelPath = modelPath;
        this.position.set(initialX, initialY);
    }

    public GameObject(String modelPath, Vector2 initialPosition) {
        this(modelPath, initialPosition.x, initialPosition.y);
    }

    public GameObject(String modelPath) {
        this(modelPath, 0, 0);
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

    public void makeInstance(Model model) {
        this.model = new ModelInstance(model, getPosition().x, height, getPosition().y);
    }

    public final ModelInstance getModelInstance() {
        return model;
    }

    public void update(float delta) {
        model.transform.setTranslation(getPosition().x, getHeight(), getPosition().y);
    }
}
