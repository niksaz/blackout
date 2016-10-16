package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.GameWorld;

public class ShellObject extends DynamicObject {
    protected ShellObject(Definition def, Model model, GameWorld gameWorld) {
        super(def, model, gameWorld);
    }

    @Override
    void updateVelocityForSecondStep() {
        body.setLinearVelocity(0, 0);
    }

    public static class Definition extends DynamicObject.Definition {
        public Definition(String modelPath, Shape shape, float initialX, float initialY) {
            super(modelPath, shape, initialX, initialY);
        }

        @Override
        public GameObject makeInstance(Model model, GameWorld gameWorld) {
            return new ShellObject(this, model, gameWorld);
        }
    }
}
