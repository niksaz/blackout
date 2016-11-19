package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.GameWorld;

public abstract class DynamicObject extends GameObject {
    public static class Animations {
        public static final String DEFAULT = "Armature|Stay";
    }

    protected final Vector2 velocity = new Vector2();

    // Appearance:
    protected final AnimationController animation;

    protected DynamicObject(Definition def, Model model, GameWorld gameWorld) {
        super(def, model, gameWorld);
        animation = new AnimationController(this.model);
        animation.setAnimation(Animations.DEFAULT, -1);
    }
    protected float animationSpeed = 1f;

    @Override
    public void updateState(float delta) {
        super.updateState(delta);
        animation.update(delta * animationSpeed);
    }

    @Override
    public void updateForFirstStep() {
        super.updateForFirstStep();
        body.setLinearVelocity(velocity);
    }

    @Override
    public void updateForSecondStep() {
        super.updateForSecondStep();
        velocity.set(body.getLinearVelocity());
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
