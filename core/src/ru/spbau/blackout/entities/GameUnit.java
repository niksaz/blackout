package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector2;


public abstract class GameUnit {
    public static final float DEFAULT_HEIGHT = 5;

    public Vector2 position = new Vector2();
    public float height = DEFAULT_HEIGHT;

    // movement:
    private Vector2 velocity = new Vector2();
    private float speed = 1;

    // appearance
    private ModelInstance model;
    private AnimationController animation;

    public GameUnit(Model model, float x, float y) {
        this.model = new ModelInstance(model, x, height, y);
        animation = new AnimationController(this.model);
        animation.setAnimation("Armature|Walk", -1);
    }

    public ModelInstance forRender(float delta) {
        update(delta);
        return model;
    }

    private void update(float delta) {
        float newX = position.x + velocity.x * speed * delta;
        float newY = position.y + velocity.y * speed * delta;

        model.transform.setToTranslation(newX, height, newY);
        animation.update(delta);
    }
}
