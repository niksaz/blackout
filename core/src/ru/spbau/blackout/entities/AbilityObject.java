package ru.spbau.blackout.entities;

import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.utils.Creator;


/**
 * Class for objects created by abilities.
 */
public abstract class AbilityObject extends DynamicObject {

    protected AbilityObject(Definition def, long uid, float x, float y) {
        super(def, uid, x, y);
    }

    /** Calls when the object contacts with another object. */
    public void beginContact(GameObject object) {}

    @Override
    public void updateForSecondStep() {
        super.updateForSecondStep();
        body.setLinearVelocity(0, 0);
    }


    public static abstract class Definition extends DynamicObject.Definition {

        private static final long serialVersionUID = 1000000000L;

        public Definition(String modelPath, Creator<Shape> shapeCreator, float mass) {
            super(modelPath, shapeCreator);
            this.mass = mass;
        }
    }
}
