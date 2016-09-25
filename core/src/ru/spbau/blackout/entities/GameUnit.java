package ru.spbau.blackout.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;


public abstract class GameUnit extends Sprite {
    private Vector2 velocity = new Vector2(0, 0);
    private float speed = 0;

    public GameUnit(Sprite sprite) {
        super(sprite);
    }

    @Override
    public void draw(Batch batch) {
        update(Gdx.graphics.getDeltaTime());
        super.draw(batch);
    }

    void update(float delta) {
        setX(getX() + velocity.x * speed * delta);
        setY(getY() + velocity.y * speed * delta);
    }


}
