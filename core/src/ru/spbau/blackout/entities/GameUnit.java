package ru.spbau.blackout.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.joints.FrictionJoint;

import ru.spbau.blackout.GameWorld;
import ru.spbau.blackout.utils.Utils;

import static ru.spbau.blackout.utils.Utils.projectVec;
import static ru.spbau.blackout.utils.Utils.sqr;

public abstract class GameUnit extends DynamicObject {
    public static class Animations extends DynamicObject.Animations {
        public static final String WALK = "Armature|Walk";
        public static final String STAY = "Armature|Stay";

        public static final float WALK_SPEED_FACTOR = 3f;
    }

    public static final float SLOW_DOWN_FACTOR = 0.02f;

    public static final float DEFAULT_LINEAR_FRICTION = 5f;
    public static final float DEFAULT_ANGULAR_FRICTION = 5f;

    // Movement:
    final private Vector2 selfVelocity = new Vector2();
    private float selfVelocityScale;
    private final FrictionJoint frictionJoint;

    protected GameUnit(Definition def, Model model, GameWorld gameWorld) {
        super(def, model, gameWorld);
        selfVelocityScale = def.selfVelocityScale;
        animation.setAnimation(Animations.STAY, -1);

        frictionJoint = gameWorld.addFriction(body, DEFAULT_LINEAR_FRICTION, DEFAULT_ANGULAR_FRICTION);
    }

    @Override
    public void updateForSecondStep() {
        super.updateForSecondStep();

        float projection = projectVec(selfVelocity, velocity);
        if (projection < 0) {
            float slowDown = -projection * SLOW_DOWN_FACTOR;

//             decrease velocity
            if (velocity.len2() <= sqr(slowDown)) {
                velocity.set(0, 0);
            } else {
                velocity.set(
                        velocity.x - Math.signum(velocity.x) * slowDown,
                        velocity.y - Math.signum(velocity.y) * slowDown
                );
            }
        }

        body.setLinearVelocity(
                selfVelocity.x,
                selfVelocity.y
        );
    }

    public final Vector2 getSelfVelocity() {
        return selfVelocity;
    }

    public void setSelfVelocity(final Vector2 vel) {
        if (Utils.isZeroVec(vel)) {
            // on stop walking
            if (!Utils.isZeroVec(selfVelocity.x, selfVelocity.y)) {
                animation.setAnimation(Animations.STAY, -1);
                animationSpeed = 1f;
            }
        } else {
            // on start walking
            if (Utils.isZeroVec(selfVelocity.x, selfVelocity.y)) {
                animation.setAnimation(Animations.WALK, -1);
            }

            animationSpeed = vel.len() * Animations.WALK_SPEED_FACTOR;
            setDirection(vel);
        }

        selfVelocity.set(vel.x * selfVelocityScale, vel.y * selfVelocityScale);
    }

    public static abstract class Definition extends DynamicObject.Definition {
        public static final float DEFAULT_SELF_VELOCITY_SCALE = 7f;

        public float selfVelocityScale = DEFAULT_SELF_VELOCITY_SCALE;

        public Definition(String modelPath, Shape shape, float initialX, float initialY) {
            super(modelPath, shape, initialX, initialY);
        }
    }
}
