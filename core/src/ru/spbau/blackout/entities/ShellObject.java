package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.Physics;

public class ShellObject extends DynamicObject {
    protected ShellObject(Definition def, Model model, Physics physics) {
        super(def, model, physics);
    }

    public static class Definition extends DynamicObject.Definition {
        public Definition(String modelPath, Shape shape, float initialX, float initialY) {
            super(modelPath, shape, initialX, initialY);
        }

        @Override
        public GameObject makeInstance(Model model, Physics physics) {
            return new ShellObject(this, model, physics);
        }
    }
}
