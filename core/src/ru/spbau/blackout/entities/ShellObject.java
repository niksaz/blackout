package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.GameWorld;
import ru.spbau.blackout.utils.Creator;

public class ShellObject extends DynamicObject {
    protected ShellObject(Definition def, Model model, GameWorld gameWorld) {
        super(def, model, gameWorld);
    }

    @Override
    public void updateForSecondStep() {
        super.updateForSecondStep();
        body.setLinearVelocity(0, 0);
    }

    public static class Definition extends DynamicObject.Definition {
        public Definition(String modelPath, Creator<Shape> shapeCreator,
                          float initialX, float initialY)
        {
            super(modelPath, shapeCreator, initialX, initialY);
        }

        @Override
        public GameObject makeInstance(Model model, GameWorld gameWorld) {
            return new ShellObject(this, model, gameWorld);
        }
    }
}
