package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;

import java.io.IOException;
import java.io.ObjectInputStream;

import ru.spbau.blackout.java8features.Optional;
import ru.spbau.blackout.utils.Creator;


public abstract class DynamicObject extends GameObject {
    /** Constant holder class to provide names for animations. */
    public static class Animations {
        protected Animations() {}
        public static final String DEFAULT = "Armature|Stay";
        public static final String DEATH = "Armature|Death";
    }


    /**
     * It is public because it's safe to update in almost any time.
     * And it allows to do stuff like <code>object.velocity.mulAdd(...)</code>.
     * It also has getter and setter to have similar interface to
     * <code>selfVelocity</code> from <code>GameUnit</code>.
     */
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


    public final Vector2 getVelocity() { return this.velocity; }
    public final void setVelocity(float x, float y) { this.velocity.set(x, y); }
    public final void setVelocity(Vector2 newVelocity) { this.setVelocity(newVelocity.x, newVelocity.y); }

    public void applyImpulse(float x, float y) {
        float mass = this.body.getMass();
        this.velocity.add(x / mass, y / mass);
    }

    public void applyImpulse(Vector2 impulse) {
        this.applyImpulse(impulse.x, impulse.y);
    }


    @Override
    public void updateState(float deltaTime) {
        super.updateState(deltaTime);
        this.animation.ifPresent(controller -> controller.update(deltaTime * this.animationSpeed));
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
