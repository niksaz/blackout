package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.GameWorld;
import ru.spbau.blackout.utils.Creator;


public class ShellObject extends DynamicObject {
    protected ShellObject(Definition def, float x, float y) {
        super(def, x, y);
    }

    @Override
    public void updateForSecondStep() {
        super.updateForSecondStep();
        body.setLinearVelocity(0, 0);
    }


    public static class Definition extends DynamicObject.Definition {
        public Definition(String modelPath, Creator<Shape> shapeCreator, float initialX, float initialY) {
            super(modelPath, shapeCreator, initialX, initialY);
        }
        /** Same as <code> Definition(String modelPath, Creator<Shape> shapeCreator, 0, 0)</code> */
        public Definition(String modelPath, Creator<Shape> shapeCreator) {
            super(modelPath, shapeCreator, 0, 0);
        }


        @Override
        public GameObject makeInstance(float x, float y) {
            return new ShellObject(this, x, y);
        }
    }
}
