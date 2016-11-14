package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.GameWorld;

public abstract class StaticObject extends GameObject {
    protected StaticObject(Definition def, Model model, GameWorld gameWorld) {
        super(def, model, gameWorld);
    }

    public static abstract class Definition extends GameObject.Definition {
        public Definition(String modelPath, Shape shape, float initialX, float initialY) {
            super(modelPath, shape, initialX, initialY);
            setFriction(0f);
        }

        @Override
        public BodyDef.BodyType getBodyType() {
            return BodyDef.BodyType.StaticBody;
        }
    }
}