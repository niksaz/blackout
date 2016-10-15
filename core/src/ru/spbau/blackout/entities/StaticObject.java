package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.box2d.BodyDef;

import ru.spbau.blackout.Physics;

public abstract class StaticObject extends GameObject {
    protected StaticObject(Definition def, Model model, Physics physics) {
        super(def, model, physics);
    }

    public static abstract class Definition extends GameObject.Definition {
        public Definition(String modelPath, float initialX, float initialY) {
            super(modelPath, initialX, initialY);
            bodyDef.type = BodyDef.BodyType.StaticBody;
        }
    }
}
