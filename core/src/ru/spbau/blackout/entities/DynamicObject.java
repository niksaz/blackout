package ru.spbau.blackout.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.joints.FrictionJoint;
import com.badlogic.gdx.physics.box2d.joints.FrictionJointDef;

import ru.spbau.blackout.GameWorld;
import ru.spbau.blackout.utils.Utils;

public abstract class DynamicObject extends GameObject {
    public static class Animations {
        public static final String WALK = "Armature|Walk";
        public static final String STAY = "Armature|Stay";
        public static final float WALK_SPEED_FACTOR = 3f;
    }

    // Movement:
    final private Vector2 selfVelocity = new Vector2();
    private float maxImpulse;

    // Appearance:
    protected final AnimationController animation;
    private float animationSpeed = 1f;

    protected DynamicObject(Definition def, Model model, GameWorld gameWorld) {
        super(def, model, gameWorld);
        maxImpulse = def.maxImpulse;
        animation = new AnimationController(this.model);
        animation.setAnimation(Animations.STAY, -1);
    }

    public final Vector2 getSelfVelocity() {
        return selfVelocity;
    }

    public void setSelfVelocity(final Vector2 vel) {
        // avoid excessive allocation
        // Vector2 old = new Vector2(selfVelocity);
        final float oldX = selfVelocity.x;
        final float oldY = selfVelocity.y;

        selfVelocity.set(vel.x, vel.y);
        if (Utils.isZeroVec(selfVelocity)) {
            // on stop walking
            if (!Utils.isZeroVec(oldX, oldY)) {
                animation.setAnimation(Animations.STAY, -1);
                animationSpeed = 1f;
            }
        } else {
            // on start walking
            if (Utils.isZeroVec(oldX, oldY)) {
                animation.setAnimation(Animations.WALK, -1);
            }

            animationSpeed = selfVelocity.len() * Animations.WALK_SPEED_FACTOR;
            setDirection(selfVelocity);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);

//        Vector2 velocity = body.getLinearVelocity();
//        body.setLinearVelocity(selfVelocity.x * maxImpulse, selfVelocity.y * maxImpulse);
        
        animation.update(delta * animationSpeed); // FIXME: shouldn't be here
    }

    public static abstract class Definition extends GameObject.Definition {
        public static final float DEFAULT_MAX_IMPULSE = 10f;

        // Movement:
        private float maxImpulse = DEFAULT_MAX_IMPULSE;

        public Definition(String modelPath, Shape shape, float initialX, float initialY) {
            super(modelPath, shape, initialX, initialY);
        }

        public float getMaxImpulse() {
            return maxImpulse;
        }

        public void setMaxImpulse(float maxImpulse) {
            this.maxImpulse = maxImpulse;
        }

        @Override
        public BodyDef.BodyType getBodyType() {
            return BodyDef.BodyType.DynamicBody;
        }
    }
}
