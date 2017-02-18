package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.utils.Uid;
import ru.spbau.blackout.utils.Utils;


public abstract class DynamicObject extends GameObject {

    /** Constant holder class to provide names for animations. */
    protected static class Animations {
        protected Animations() {}
        public static final String STAY = "Armature|Stay";
    }

    /**
     * Object's natural velocity.
     * @see ru.spbau.blackout.worlds.GameWorld
     */
    public final Vector2 velocity = new Vector2();
    /** @see ru.spbau.blackout.worlds.GameWorld */
    public final Vector2 temporaryVelocity = new Vector2();

    // Appearance:
    @Nullable protected final AnimationController animation;
    protected float animationSpeed = 1f;


    /** Construct DynamicObject at the giving touchPos. */
    protected DynamicObject(Definition def, Uid uid, float x, float y) {
        super(def, uid, x, y);

        if (modelInstance != null) {
            animation = new AnimationController(modelInstance);
            animation.setAnimation(Animations.STAY, -1);
        } else {
            animation = null;
        }
    }

    @Override
    public void kill() {
        super.kill();
        velocity.setZero();
        temporaryVelocity.setZero();
        body.setLinearVelocity(Vector2.Zero);
    }

    public final void applyImpulse(float x, float y) {
        float mass = getMass();
        velocity.add(x / mass, y / mass);
    }

    public final void applyImpulse(Vector2 impulse) {
        applyImpulse(impulse.x, impulse.y);
    }

    public final void applyTemporaryImpulse(float x, float y) {
        float mass = getMass();
        temporaryVelocity.add(x / mass, y / mass);
    }

    public final void applyTemporaryImpulse(Vector2 impulse) {
        applyTemporaryImpulse(impulse.x, impulse.y);
    }

    @Override
    public void updateGraphics(float delta) {
        super.updateGraphics(delta);
        if (animation != null) {
            animation.update(delta * animationSpeed);
        }
    }

    /**
     * Must be called only by {@link ru.spbau.blackout.worlds.ServerGameWorld} before
     * the first step processing.
     */
    public final void prepareForFirstStep() {
        body.setLinearVelocity(velocity);
        // to take into account velocity changes during the step
        velocity.setZero();
    }

    /**
     * Must be called only by {@link ru.spbau.blackout.worlds.ServerGameWorld} before
     * the second step processing.
     */
    public final void prepareForSecondStep() {
        velocity.add(body.getLinearVelocity());
        body.setLinearVelocity(temporaryVelocity);
        temporaryVelocity.setZero();
    }

    @Override
    public void getState(ObjectOutputStream out) throws IOException, ClassNotFoundException {
        super.getState(out);
        out.writeFloat(animationSpeed);
    }

    @Override
    public void setState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.setState(in);
        animationSpeed = in.readFloat();
    }


    /** Definition for objects which have Dynamic body type. */
    public static abstract class Definition extends GameObject.Definition {

        private static final long serialVersionUID = 1000000000L;

        public Definition(@Nullable String modelPath, Creator<Shape> shapeCreator, @Nullable String deathEffectPath) {
            super(modelPath, shapeCreator, deathEffectPath);
        }

        @Override
        public final BodyDef.BodyType getBodyType() {
            return BodyDef.BodyType.DynamicBody;
        }
    }
}
