package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.GameWorld;
import ru.spbau.blackout.utils.Creator;

public class Decoration extends StaticObject {
    protected Decoration(Definition def, float x, float y) {
        super(def, x, y);
    }


    public static class Definition extends StaticObject.Definition {
        public Definition(String modelPath, Creator<Shape> shapeCreator, float initialX, float initialY) {
            super(modelPath, shapeCreator, initialX, initialY);
        }


        @Override
        public GameObject makeInstance(float x, float y) {
            return new Decoration(this, x, y);
        }
    }
}
