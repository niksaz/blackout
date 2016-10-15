package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;

import ru.spbau.blackout.Physics;

public class GameUnit extends DynamicObject {
    protected GameUnit(Definition def, Model model, Physics physics) {
        super(def, model, physics);
    }

    public static class Definition extends DynamicObject.Definition {
        public Definition(String modelPath, float initialX, float initialY) {
            super(modelPath, initialX, initialY);
        }

        @Override
        public GameObject makeInstance(Model model, Physics physics) {
            return new GameUnit(this, model, physics);
        }
    }
}
