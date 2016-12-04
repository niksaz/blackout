package ru.spbau.blackout.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;

import java.io.IOException;
import java.io.ObjectInputStream;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.utils.Utils;

import static ru.spbau.blackout.java8features.Functional.foreach;


/**
 * Unit is a dynamic object which can move by itself and cast abilities.
 * Also it has friction.
 */
public abstract class GameUnit extends DynamicObject implements Damageable {
    /** Constant holder class to provide names for animations. */
    public static class Animations extends DynamicObject.Animations {
        protected Animations() {}
        public static final String WALK = "Armature|Walk";
        public static final String STAY = "Armature|Stay";
        public static final float WALK_ANIM_SPEED_FACTOR = 3f;
    }

    public static final float SELF_RESISTANCE_FACTOR = 0.02f;
    public static final float LINEAR_FRICTION = 0.002f;


    private final Vector2 selfVelocity = new Vector2();
    private float speed;
    transient private final Ability[] abilities;
    private float health;
    private float maxHealth;


    protected GameUnit(Definition def, float x, float y, GameContext context) {
        super(def, x, y);
        this.speed = def.speed;
        this.animation.ifPresent(controller -> controller.setAnimation(Animations.STAY, -1));
        this.abilities = def.abilities;

        this.maxHealth = def.maxHealth;
        this.health = this.maxHealth;

        for (Ability ability : abilities) {
            ability.initialize(this);
        }
    }


    public final Ability getAbility(int num) {
        return this.abilities[num];
    }


    @Override
    public void updateForFirstStep() {
        // apply friction
        if (!Utils.isZeroVec(this.velocity)){
            float k = 1f - (this.getMass() * LINEAR_FRICTION) / this.velocity.len();
            if (k < 0) k = 0;
            this.velocity.scl(k);
        }
        super.updateForFirstStep();
    }

    /** See <code>GameWorld</code> documentation */
    @Override
    public void updateForSecondStep() {
        super.updateForSecondStep();

        // Resistance to external velocity by unit (selfVelocity)
        if (!Utils.isZeroVec(this.velocity)) {
            // some inefficient, but clear pseudocode in comments:
            // float proj = |projection of selfVelocity on velocity|
            // float k = proj / |velocity| * SLOW_DOWN_FACTOR
            float k = this.selfVelocity.dot(this.velocity) / this.velocity.len2() * SELF_RESISTANCE_FACTOR;
            // don't increase velocity
            if (k > 0) k = 0;
            // don't accelerate to the opposite direction
            if (k < -1) k = -1;
            this.velocity.mulAdd(this.velocity, k);
        }

        this.body.setLinearVelocity(this.selfVelocity);
    }

    public final synchronized Vector2 getSelfVelocity() {
        // to ensure that nobody will change it outside avoiding setSelfVelocity method.
        return new Vector2(this.selfVelocity);
    }

    @Override
    public Object inplaceDeserialize(ObjectInputStream in) throws IOException, ClassNotFoundException {
        GameUnit other = (GameUnit) super.inplaceDeserialize(in);
        //this.selfVelocity.set(other.selfVelocity);

//        this.speed = other.speed;
        return other;
    }


    @Override
    public void damage(float damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.kill();
        }
    }

    @Override
    public float getHealth() { return this.health; }

    public float getMaxHealth() { return this.maxHealth; }


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

            this.animationSpeed = newVelocity.len() * Animations.WALK_ANIM_SPEED_FACTOR;
            this.setDirection(newVelocity);
        }

        this.selfVelocity.set(newVelocity.x * speed, newVelocity.y * speed);
    }

    public void setSelfVelocity(float x, float y) { setSelfVelocity(new Vector2(x, y)); }


    /** Definition for units. Loads abilities. */
    public static abstract class Definition extends DynamicObject.Definition {
        public static final float DEFAULT_SPEED = 8f;

        public float speed = DEFAULT_SPEED;
        public Ability[] abilities;
        public float maxHealth;


        public Definition(String modelPath, Creator<Shape> shapeCreator, float initialX, float initialY,
                          Ability[] abilities, float maxHealth) {
            super(modelPath, shapeCreator, initialX, initialY);
            this.abilities = abilities;
            this.maxHealth = maxHealth;
        }


        @Override
        public void load(GameContext context) {
            super.load(context);
            for (Ability ability : abilities) {
                ability.load(context);
            }
        }

        @Override
        public void doneLoading(GameContext context) {
            super.doneLoading(context);
            for (Ability ability : abilities) {
                ability.doneLoading(context);
            }
        }
    }
}
