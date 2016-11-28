package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;

import java.io.IOException;
import java.io.ObjectInputStream;

import ru.spbau.blackout.GameWorld;
import ru.spbau.blackout.java8features.Optional;
import ru.spbau.blackout.utils.Creator;

public abstract class DynamicObject extends GameObject {
    public static class Animations {
        protected Animations() {}
        public static final String DEFAULT = "Armature|Stay";
    }


    public final Vector2 velocity = new Vector2();


    // Appearance:
    /** It is empty on server */
    transient protected final Optional<AnimationController> animation;
    transient protected float animationSpeed = 1f;


    /** Construct DynamicObject at the giving position. */
    protected DynamicObject(Definition def, float x, float y) {
        super(def, x, y);

        animation = this.model.map(AnimationController::new);
        animation.ifPresent(controller -> controller.setAnimation(Animations.DEFAULT, -1));
    }


    @Override
    public void updateState(float deltaTime) {
        super.updateState(deltaTime);
        animation.ifPresent(controller -> controller.update(deltaTime * animationSpeed));
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

    @Override
    public Object inplaceDeserialize(ObjectInputStream in) throws IOException, ClassNotFoundException {
        DynamicObject other = (DynamicObject) super.inplaceDeserialize(in);
        //this.velocity.set(other.velocity);
        //this.animationSpeed = other.animationSpeed;  // FIXME: probably should be removed
        return other;
    }


    /** Definition for objects which have Dynamic body type. */
    public static abstract class Definition extends GameObject.Definition {
        public Definition(String modelPath, Creator<Shape> shapeCreator, float initialX, float initialY) {
            super(modelPath, shapeCreator, initialX, initialY);
        }

        @Override
        public BodyDef.BodyType getBodyType() {
            return BodyDef.BodyType.DynamicBody;
        }
    }
}
