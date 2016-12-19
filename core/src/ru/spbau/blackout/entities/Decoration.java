package ru.spbau.blackout.entities;

import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.utils.Creator;


public class Decoration extends StaticObject {
    protected Decoration(Definition def, long uid, float x, float y) {
        super(def, uid, x, y);
    }


    public static class Definition extends StaticObject.Definition {
        public Definition(String modelPath, Creator<Shape> shapeCreator) {
            super(modelPath, shapeCreator);
        }


        @Override
        public GameObject makeInstance(long uid, float x, float y) {
            return new Decoration(this, uid, x, y);
        }
    }
}
