package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.Physics;

public class ShellObject extends DynamicObject {
    protected ShellObject(Definition def, Model model, Physics physics) {
        super(def, model, physics);
    }

    public static class Definition extends DynamicObject.Definition {
        public Definition(String modelPath, float initialX, float initialY) {
            super(modelPath, initialX, initialY);
        }

        public Definition(String modelPath, Vector2 initialPosition) {
            this(modelPath, initialPosition.x, initialPosition.y);
        }

        public Definition(String modelPath) {
            this(modelPath, 0, 0);
        }

        @Override
        public GameObject makeInstance(Model model, Physics physics) {
            return new ShellObject(this, model, physics);
        }
    }
}
