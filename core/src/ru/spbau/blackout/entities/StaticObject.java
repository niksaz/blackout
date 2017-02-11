package ru.spbau.blackout.entities;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.utils.Uid;


public abstract class StaticObject extends GameObject {
    public StaticObject(GameObject.Definition def, Uid uid, float x, float y) {
        super(def, uid, x, y);
    }

    // We don't need to updatePhysics velocity for static objects
    @Override
    public void updateForSecondStep() {}
    @Override
    public void updateForFirstStep() {}

    /** Definition for objects which have Static body type. */
    public static abstract class Definition extends GameObject.Definition {

        private static final long serialVersionUID = 1000000000L;

        public Definition(String modelPath, Creator<Shape> shapeCreator) {
            super(modelPath, shapeCreator, null);
        }

        @Override
        public BodyDef.BodyType getBodyType() {
            return BodyDef.BodyType.StaticBody;
        }
    }
}
