package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.joints.FrictionJoint;

import ru.spbau.blackout.GameWorld;
import ru.spbau.blackout.utils.Utils;

public abstract class GameUnit extends DynamicObject {
    public static class Animations extends DynamicObject.Animations {
        public static final String WALK = "Armature|Walk";
        public static final String STAY = "Armature|Stay";

        public static final float WALK_SPEED_FACTOR = 3f;
    }

    public static final float DEFAULT_LINEAR_FRICTION = 5f;
    public static final float DEFAULT_ANGULAR_FRICTION = 5f;

    // Movement:
    final private Vector2 selfVelocity = new Vector2();
    private float selfVelocityScale;
    private final FrictionJoint frictionJoint;

    protected GameUnit(Definition def, Model model, GameWorld gameWorld) {
        super(def, model, gameWorld);
        selfVelocityScale = def.maxSelfVelocity;
        animation.setAnimation(Animations.STAY, -1);

        frictionJoint = gameWorld.addFriction(body, DEFAULT_LINEAR_FRICTION, DEFAULT_ANGULAR_FRICTION);
    }

    @Override
    void updateVelocityForSecondStep() {
        body.setLinearVelocity(
                selfVelocity.x * selfVelocityScale,
                selfVelocity.y * selfVelocityScale
        );
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

    public static abstract class Definition extends DynamicObject.Definition {
        public static final float DEFAULT_MAX_SELF_VELOCITY = 10f;

        public float maxSelfVelocity = DEFAULT_MAX_SELF_VELOCITY;

        public Definition(String modelPath, Shape shape, float initialX, float initialY) {
            super(modelPath, shape, initialX, initialY);
        }
    }
}
