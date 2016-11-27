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
import ru.spbau.blackout.utils.Creator;

public abstract class DynamicObject extends GameObject {
    protected static final class NullableAnimationController {
        private final AnimationController animation;

        public NullableAnimationController(ModelInstance model) {
            if (model == null) {
                this.animation = null;
            } else {
                this.animation = new AnimationController(model);
            }
        }

        public void update(float deltaTime) {
            if (animation != null) {
                animation.update(deltaTime);
            }
        }

        public void setAnimation(String id, int loopCount) {
            if (animation != null) {
                animation.setAnimation(id, loopCount);
            }
        }
    }

    public static class Animations {
        public static final String DEFAULT = "Armature|Stay";

        protected Animations() {};
    }

    protected final Vector2 velocity = new Vector2();

    // Appearance:
    transient protected final NullableAnimationController animation;
    protected float animationSpeed = 1f;

    protected DynamicObject(Definition def, Model model, GameWorld gameWorld) {
        super(def, model, gameWorld);

        animation = new NullableAnimationController(this.model);
        animation.setAnimation(Animations.DEFAULT, -1);
    }

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

    @Override
    public Object inplaceDeserialize(ObjectInputStream in) throws IOException, ClassNotFoundException {
        DynamicObject other = (DynamicObject) super.inplaceDeserialize(in);
        this.velocity.set(other.velocity);
        this.animationSpeed = other.animationSpeed;  // FIXME: probably should be removed
        return other;
    }

    public static abstract class Definition extends GameObject.Definition {
        public Definition(String modelPath, Creator<Shape> shapeCreator,
                          float initialX, float initialY)
        {
            super(modelPath, shapeCreator, initialX, initialY);
        }

        @Override
        public BodyDef.BodyType getBodyType() {
            return BodyDef.BodyType.DynamicBody;
        }
    }
}
