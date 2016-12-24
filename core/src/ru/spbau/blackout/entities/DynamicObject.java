package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ru.spbau.blackout.java8features.Optional;
import ru.spbau.blackout.utils.Creator;


public abstract class DynamicObject extends GameObject {
    /** Constant holder class to provide names for animations. */
    public static class Animations {
        protected Animations() {}
        public static final String DEFAULT = "Armature|Stay";
    }


    /**
     * It is public because it's safe to updateState in almost any time.
     * And it allows to do stuff like <code>object.velocity.mulAdd(...)</code>.
     * It also has getter and setter to have similar interface to
     * <code>selfVelocity</code> from <code>GameUnit</code>.
     */
    public final Vector2 velocity = new Vector2();


    // Appearance:
    /** It is empty on server */
    protected final Optional<AnimationController> animation;
    protected float animationSpeed = 1f;


    /** Construct DynamicObject at the giving position. */
    protected DynamicObject(Definition def, long uid, float x, float y) {
        super(def, uid, x, y);

        animation = this.model.map(AnimationController::new);
        animation.ifPresent(controller -> controller.setAnimation(Animations.DEFAULT, -1));
    }

    @Override
    public void kill() {
        super.kill();
        velocity.setZero();
        body.setLinearVelocity(Vector2.Zero);
    }

    public final Vector2 getVelocity() { return this.velocity; }
    public final void setVelocity(float x, float y) { this.velocity.set(x, y); }
    public final void setVelocity(Vector2 newVelocity) { this.setVelocity(newVelocity.x, newVelocity.y); }

    public void applyImpulse(float x, float y) {
        float mass = this.getMass();
        this.velocity.add(x / mass, y / mass);
    }

    public void applyImpulse(Vector2 impulse) {
        this.applyImpulse(impulse.x, impulse.y);
    }


    @Override
    public void updateGraphics(float delta) {
        super.updateGraphics(delta);
        this.animation.ifPresent(controller -> controller.update(delta * this.animationSpeed));
    }

    @Override
    public void updateForFirstStep() {
        this.body.setLinearVelocity(this.velocity);
        // to take into account velocity changes during the step
        this.velocity.set(0, 0);
    }

    @Override
    public void updateForSecondStep() {
        this.velocity.add(this.body.getLinearVelocity());
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
