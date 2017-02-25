package ru.spbau.blackout.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import ru.spbau.blackout.serializationutils.EfficientInputStream;
import ru.spbau.blackout.serializationutils.EfficientOutputStream;
import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.utils.Uid;
import ru.spbau.blackout.utils.Utils;

/**
 * Unit is a dynamic object which can move by itself and cast abilities.
 * Also it has friction.
 */
public abstract class GameUnit extends DynamicObject {

    private static final float LINEAR_FRICTION = 0.002f;

    /** Constant holder class to provide names for animations. */
    protected static class UnitAnimations extends DynamicObject.Animations {
        protected UnitAnimations() {}
        public static final String WALK = "Armature|Walk";
        public static final float WALK_ANIM_SPEED_FACTOR = 3f;
    }

    private static final float SELF_RESISTANCE_FACTOR = 0.02f;

    private final Vector2 selfVelocity = new Vector2();
    private float speed;

    protected GameUnit(Definition def, Uid uid, float x, float y) {
        super(def, uid, x, y);
        speed = def.speed;
    }


    @Override
    public void kill() {
        super.kill();
        setSelfVelocity(Vector2.Zero);
    }

    @Override
    public void updateBeforeFirstStep() {
        super.updateBeforeFirstStep();

        // apply friction
        if (!Utils.isZeroVec(velocity)){
            float k = 1f - (getMass() * LINEAR_FRICTION) / velocity.len();
            if (k < 0) k = 0;
            velocity.scl(k);
        }

        // Resistance to external velocity by unit (selfVelocity)
        if (!Utils.isZeroVec(velocity)) {
            // some inefficient, but clear pseudocode in comments:
            // float proj = |projection of selfVelocity on velocity|
            // float k = proj / |velocity| * SLOW_DOWN_FACTOR
            float k = selfVelocity.dot(velocity) / velocity.len2() * SELF_RESISTANCE_FACTOR;
            // don't increase velocity
            if (k > 0) k = 0;
            // don't accelerate to the opposite direction
            if (k < -1) k = -1;
            velocity.mulAdd(velocity, k);
        }
    }

    /** See <code>GameWorld</code> documentation */
    @Override
    public void updateBeforeSecondStep() {
        super.updateBeforeSecondStep();
        temporaryVelocity.add(selfVelocity.x * speed, selfVelocity.y * speed);
    }

    public final synchronized Vector2 getSelfVelocity() {
        // to ensure that nobody will change it outside avoiding setSelfVelocity method.
        return new Vector2(selfVelocity);
    }

    @Override
    public void getState(EfficientOutputStream out) throws IOException {
        super.getState(out);
        out.writeFloat(speed);
        out.writeVector2(getSelfVelocity());
    }

    @Override
    public void setState(EfficientInputStream in) throws IOException {
        super.setState(in);
        speed = in.readFloat();
        setSelfVelocity(in.readVector2());
    }

    public void setSelfVelocity(Vector2 newVelocity) {
        if (Utils.isZeroVec(newVelocity)) {
            // on stop walking
            if (!Utils.isZeroVec(selfVelocity)) {
                if (animation != null) {
                    animation.setAnimation(Animations.STAY, -1);
                }
                animationSpeed = 1f;
            }
        } else {
            // on start walking
            if (Utils.isZeroVec(selfVelocity)) {
                if (animation != null) {
                    animation.setAnimation(UnitAnimations.WALK, -1);
                }
            }

            animationSpeed = newVelocity.len() * UnitAnimations.WALK_ANIM_SPEED_FACTOR;
            setDirection(newVelocity);
        }

        selfVelocity.set(newVelocity.x, newVelocity.y);
    }

    /** Definition for units. Loads abilities. */
    public static abstract class Definition extends DynamicObject.Definition {

        private static final long serialVersionUID = 1000000000L;

        public static final float DEFAULT_SPEED = 8f;


        public float speed = DEFAULT_SPEED;


        public Definition(@Nullable String modelPath, Creator<Shape> shapeCreator, @Nullable String deathEffectPath) {
            super(modelPath, shapeCreator, deathEffectPath);
        }
    }
}
