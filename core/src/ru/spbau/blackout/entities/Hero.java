package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector2;

public class Hero extends GameUnit {
    public Hero(String modelPath, float initialX, float initialY) {
        super(modelPath, initialX, initialY);
    }

    public Hero(String modelPath, Vector2 initialPosition) {
        this(modelPath, initialPosition.x, initialPosition.y);
    }

    @Override
    protected void onInstance() {
        // FIXME: just for test:
        animation.setAnimation("Armature|Walk", -1);
    }
}
