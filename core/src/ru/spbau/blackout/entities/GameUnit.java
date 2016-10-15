package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.joints.FrictionJoint;

import ru.spbau.blackout.GameWorld;

public abstract class GameUnit extends DynamicObject {
    private FrictionJoint friction;

    protected GameUnit(Definition def, Model model, GameWorld gameWorld) {
        super(def, model, gameWorld);
        friction = gameWorld.addFriction(body, 5f, 1f);
    }

    public static abstract class Definition extends DynamicObject.Definition {
        public Definition(String modelPath, Shape shape, float initialX, float initialY) {
            super(modelPath, shape, initialX, initialY);

        }
    }
}
