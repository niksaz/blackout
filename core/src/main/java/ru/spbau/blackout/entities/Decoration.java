package ru.spbau.blackout.entities;

import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.utils.Uid;


public class Decoration extends StaticObject {
    protected Decoration(Definition def, Uid uid, float x, float y) {
        super(def, uid, x, y);
    }


    public static class Definition extends StaticObject.Definition {

        private static final long serialVersionUID = 1000000000L;

        public Definition(String modelPath, Creator<Shape> shapeCreator) {
            super(modelPath, shapeCreator, null);
        }


        @Override
        public GameObject makeInstance(Uid uid, float x, float y) {
            return new Decoration(this, uid, x, y);
        }
    }
}
