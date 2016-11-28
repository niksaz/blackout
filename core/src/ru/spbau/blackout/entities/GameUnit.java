package ru.spbau.blackout.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;

import java.io.IOException;
import java.io.ObjectInputStream;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.utils.Utils;


/**
 * Unit is a dynamic object which can move by itself and cast abilities.
 * Also it has friction.
 */
public abstract class GameUnit extends DynamicObject {
    /** Constant holder class to provide names for animations. */
    public static class Animations extends DynamicObject.Animations {
        protected Animations() {}
        public static final String WALK = "Armature|Walk";
        public static final String STAY = "Armature|Stay";
        public static final float WALK_SPEED_FACTOR = 3f;
    }

    public static final float SLOW_DOWN_FACTOR = 0.02f;

    public static final float DEFAULT_LINEAR_FRICTION = 5f;
    public static final float DEFAULT_ANGULAR_FRICTION = 5f;

    // Movement:
    private final Vector2 selfVelocity = new Vector2();
    private float speed;
    transient private final Ability[] abilities;


    protected GameUnit(Definition def, float x, float y, GameContext context) {
        super(def, x, y);
        this.speed = def.speed;
        this.animation.ifPresent(controller -> controller.setAnimation(Animations.STAY, -1));
        this.abilities = def.abilities;

        // add friction for the unit
        context.gameWorld().addFriction(body, DEFAULT_LINEAR_FRICTION, DEFAULT_ANGULAR_FRICTION);

        for (Ability ability : abilities) {
            ability.doneLoading(context, this);
        }
    }


    public final Ability getAbility(int num) {
        return this.abilities[num];
    }

    /** See <code>GameWorld</code> documentation */
    @Override
    public void updateForSecondStep() {
        super.updateForSecondStep();

        // Resistance to external velocity by unit (selfVelocity)
        if (!Utils.isZeroVec(velocity)) {
            // some inefficient, but clear pseudocode in comments:
            // float proj = |projection of selfVelocity on velocity|
            // float k = proj / |velocity| * SLOW_DOWN_FACTOR
            float k = this.selfVelocity.dot(velocity) / this.velocity.len2() * SLOW_DOWN_FACTOR;
            // don't increase velocity
            if (k > 0) { k = 0; }
            // don't accelerate to the opposite direction
            if (k < -1) { k = -1; }
            this.velocity.mulAdd(this.velocity, k);
        }

        body.setLinearVelocity(
                selfVelocity.x,
                selfVelocity.y
        );
    }

    public final synchronized Vector2 getSelfVelocity() {
        // to ensure that nobody will change it outside avoiding setSelfVelocity method.
        return new Vector2(selfVelocity);
    }

    @Override
    public Object inplaceDeserialize(ObjectInputStream in) throws IOException, ClassNotFoundException {
        GameUnit other = (GameUnit) super.inplaceDeserialize(in);
        //this.selfVelocity.set(other.selfVelocity);

//        this.speed = other.speed;
        return other;
    }


    public void setSelfVelocity(Vector2 newVelocity) {
        if (Utils.isZeroVec(newVelocity)) {
            // on stop walking
            if (!Utils.isZeroVec(this.selfVelocity)) {
                this.animation.ifPresent(controller -> controller.setAnimation(Animations.STAY, -1));
                this.animationSpeed = 1f;
            }
        } else {
            // on start walking
            if (Utils.isZeroVec(this.selfVelocity)) {
                this.animation.ifPresent(controller -> controller.setAnimation(Animations.WALK, -1));
            }

            this.animationSpeed = newVelocity.len() * Animations.WALK_SPEED_FACTOR;
            this.setDirection(newVelocity);
        }

        this.selfVelocity.set(newVelocity.x * speed, newVelocity.y * speed);
    }

    public void setSelfVelocity(float x, float y) { setSelfVelocity(new Vector2(x, y)); }


    /** Definition for units. Loads abilities. */
    public static abstract class Definition extends DynamicObject.Definition {
        public static final float DEFAULT_SPEED = 7f;

        public float speed = DEFAULT_SPEED;
        public Ability[] abilities;

        public Definition(String modelPath, Creator<Shape> shapeCreator, float initialX, float initialY,
                          Ability[] abilities) {
            super(modelPath, shapeCreator, initialX, initialY);
            this.abilities = abilities;
        }


        @Override
        public void load(GameContext context) {
            super.load(context);
            for (Ability ability : abilities) {
                ability.load(context);
            }
        }
    }
}
