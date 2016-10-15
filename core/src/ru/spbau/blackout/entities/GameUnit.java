package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.Physics;

public abstract class GameUnit extends DynamicObject {
    protected GameUnit(Definition def, Model model, Physics physics) {
        super(def, model, physics);
    }

    public static abstract class Definition extends DynamicObject.Definition {
        public Definition(String modelPath, Shape shape, float initialX, float initialY) {
            super(modelPath, shape, initialX, initialY);

        }
    }
}
