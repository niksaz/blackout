package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.GameWorld;

public class Hero extends GameUnit {
    protected Hero(Definition def, Model model, GameWorld gameWorld) {
        super(def, model, gameWorld);
//        body.applyForceToCenter(0, 1000, true);
        body.applyLinearImpulse(0, 20, getPosition().x, getPosition().y, false);
    }

    public static class Definition extends GameUnit.Definition {
        public Definition(String modelPath, Shape shape, float initialX, float initialY) {
            super(modelPath, shape, initialX, initialY);
        }

        @Override
        public GameObject makeInstance(Model model, GameWorld gameWorld) {
            return new Hero(this, model, gameWorld);
        }
    }
}
