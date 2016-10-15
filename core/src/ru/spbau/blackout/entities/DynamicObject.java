package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.GameWorld;
import ru.spbau.blackout.utils.Utils;

public abstract class DynamicObject extends GameObject {
    public static class Animations {
        public static final String DEFAULT = "Armature|Stay";
    }

    // Appearance:
    protected final AnimationController animation;
    protected float animationSpeed = 1f;

    protected DynamicObject(Definition def, Model model, GameWorld gameWorld) {
        super(def, model, gameWorld);
        animation = new AnimationController(this.model);
        animation.setAnimation(Animations.DEFAULT, -1);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        animation.update(delta * animationSpeed); // FIXME: shouldn't be here
    }

    public static abstract class Definition extends GameObject.Definition {
        public Definition(String modelPath, Shape shape, float initialX, float initialY) {
            super(modelPath, shape, initialX, initialY);
        }

        @Override
        public BodyDef.BodyType getBodyType() {
            return BodyDef.BodyType.DynamicBody;
        }
    }
}
