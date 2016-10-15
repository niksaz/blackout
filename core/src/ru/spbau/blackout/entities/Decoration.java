package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.GameWorld;

public class Decoration extends StaticObject {
    protected Decoration(Definition def, Model model, GameWorld gameWorld) {
        super(def, model, gameWorld);
    }

    public static class Definition extends StaticObject.Definition {
        public Definition(String modelPath, Shape shape, float initialX, float initialY) {
            super(modelPath, shape, initialX, initialY);
        }

        @Override
        public GameObject makeInstance(Model model, GameWorld gameWorld) {
            return new Decoration(this, model, gameWorld);
        }
    }
}
