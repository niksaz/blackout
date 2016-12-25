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


public abstract class DynamicObject extends GameObject {
    /** Constant holder class to provide names for animations. */
    public static class Animations {
        protected Animations() {}
        public static final String STAY = "Armature|Stay";
    }


    /**
     * It is public because it's safe to updatePhysics in almost any time.
     * And it allows to do stuff like <code>object.velocity.mulAdd(...)</code>.
     * It also has getter and setter to have similar interface to
     * <code>selfVelocity</code> from <code>GameUnit</code>.
     */
    public final Vector2 velocity = new Vector2();


    // Appearance:
    /** It is empty on server */
    @Nullable protected final AnimationController animation;
    protected float animationSpeed = 1f;


    /** Construct DynamicObject at the giving touchPos. */
    protected DynamicObject(Definition def, long uid, float x, float y) {
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
        body.setLinearVelocity(Vector2.Zero);
    }

    public final Vector2 getVelocity() { return velocity; }
    public final void setVelocity(float x, float y) { velocity.set(x, y); }
    public final void setVelocity(Vector2 newVelocity) { setVelocity(newVelocity.x, newVelocity.y); }

    public void applyImpulse(float x, float y) {
        float mass = getMass();
        velocity.add(x / mass, y / mass);
    }

    public void applyImpulse(Vector2 impulse) {
        applyImpulse(impulse.x, impulse.y);
    }


    @Override
    public void updateGraphics(float delta) {
        super.updateGraphics(delta);
        if (animation != null) {
            animation.update(delta * animationSpeed);
        }
    }

    @Override
    public void updateForFirstStep() {
        body.setLinearVelocity(velocity);
        // to take into account velocity changes during the step
        velocity.set(0, 0);
    }

    @Override
    public void updateForSecondStep() {
        velocity.add(body.getLinearVelocity());
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

        public Definition(String modelPath, Creator<Shape> shapeCreator) {
            super(modelPath, shapeCreator);
        }

        @Override
        public BodyDef.BodyType getBodyType() {
            return BodyDef.BodyType.DynamicBody;
        }
    }
}
