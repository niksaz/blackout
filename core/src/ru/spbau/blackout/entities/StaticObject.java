package ru.spbau.blackout.entities;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.utils.Creator;


public abstract class StaticObject extends GameObject {
    public StaticObject(GameObject.Definition def, long uid, float x, float y) {
        super(def, uid, x, y);
    }

    // We don't need to update velocity for static objects
    @Override
    public void updateForSecondStep() {}
    @Override
    public void updateForFirstStep() {}

    /** Definition for objects which have Static body type. */
    public static abstract class Definition extends GameObject.Definition {
        public Definition(String modelPath, Creator<Shape> shapeCreator) {
            super(modelPath, shapeCreator);
        }

        @Override
        public BodyDef.BodyType getBodyType() {
            return BodyDef.BodyType.StaticBody;
        }
    }
}
