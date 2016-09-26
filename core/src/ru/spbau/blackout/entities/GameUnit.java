package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.sun.javafx.sg.prism.NGShape;


public abstract class GameUnit {
    public static final float DEFAULT_HEIGHT = 5;

    public Vector2 position = new Vector2();
    public float height = DEFAULT_HEIGHT;

    // movement:
    private Vector2 velocity = new Vector2();
    private float speed = 0;

    // appearance
    private Model model;
    public ModelInstance modelInstance;

    public GameUnit(Model model, float x, float y) {
        this.model = model;
        modelInstance = new ModelInstance(model, x, height, y);
    }

    void update(float delta) {
        float newX = position.x + velocity.x * speed * delta;
        float newY = position.y + velocity.y * speed * delta;

        modelInstance.transform.setToTranslation(newX, height, newY);
    }
}
