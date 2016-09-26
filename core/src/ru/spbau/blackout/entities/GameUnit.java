package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;


public abstract class GameUnit {
    public static final float DEFAULT_HEIGHT = 5;

    public Vector2 position = new Vector2();
    public float height = DEFAULT_HEIGHT;

    // movement:
    private Vector2 velocity = new Vector2();
    private float speed = 1;

    // appearance
    private ModelInstance modelInstance;

    public GameUnit(Model model, float x, float y) {
        modelInstance = new ModelInstance(model, x, height, y);
        modelInstance.transform.scale(0.001f, 0.001f, 0.001f);
    }

    public ModelInstance forRender(float delta) {
        update(delta);
        return modelInstance;
    }

    private void update(float delta) {
        float newX = position.x + velocity.x * speed * delta;
        float newY = position.y + velocity.y * speed * delta;

        modelInstance.transform.setToTranslation(newX, height, newY);
    }
}
