package ru.spbau.blackout.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.utils.Utils;


/**
 * Unit is a dynamic object which can move by itself and cast abilities.
 * Also it has friction.
 */
public abstract class GameUnit extends DynamicObject implements Damageable  {

    /** Constant holder class to provide names for animations. */
    public static class UnitAnimations extends DynamicObject.Animations {
        protected UnitAnimations() {}
        public static final String WALK = "Armature|Walk";
        public static final String STAY = "Armature|Stay";
        public static final float WALK_ANIM_SPEED_FACTOR = 3f;
    }

    public static final float SELF_RESISTANCE_FACTOR = 0.02f;
    public static final float LINEAR_FRICTION = 0.002f;


    private final Vector2 selfVelocity = new Vector2();
    private float speed;
    private final List<Ability> abilities;
    private float health;
    private float maxHealth;


    protected GameUnit(Definition def, long uid, float x, float y) {
        super(def, uid, x, y);
        speed = def.speed;
        animation.ifPresent(controller -> controller.setAnimation(UnitAnimations.STAY, -1));
        abilities = Arrays.asList(def.abilities);

        maxHealth = def.maxHealth;
        health = maxHealth;

        for (Ability ability : abilities) {
            ability.initialize(this);
        }
    }


    public final Ability getAbility(int num) {
        return abilities.get(num);
    }

    public void castAbility(int abilityNum, Vector2 target) {
        Ability ability = getAbility(abilityNum);
        ability.cast(target);
        // TODO: cast animation
    }

    @Override
    public void kill() {
        super.kill();
        setSelfVelocity(Vector2.Zero);

    }

    @Override
    public void updateState(float delta) {
        super.updateState(delta);
        for (Ability ability : abilities) {
            ability.charge(delta);
        }
    }

    @Override
    public void updateForFirstStep() {
        // apply friction
        if (!Utils.isZeroVec(velocity)){
            float k = 1f - (getMass() * LINEAR_FRICTION) / velocity.len();
            if (k < 0) k = 0;
            velocity.scl(k);
        }
        super.updateForFirstStep();
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
            float k = selfVelocity.dot(velocity) / velocity.len2() * SELF_RESISTANCE_FACTOR;
            // don't increase velocity
            if (k > 0) k = 0;
            // don't accelerate to the opposite direction
            if (k < -1) k = -1;
            velocity.mulAdd(velocity, k);
        }

        body.setLinearVelocity(selfVelocity.x * speed, selfVelocity.y * speed);
    }

    public final synchronized Vector2 getSelfVelocity() {
        // to ensure that nobody will change it outside avoiding setSelfVelocity method.
        return new Vector2(selfVelocity);
    }

    @Override
    public void getState(ObjectOutputStream out) throws IOException, ClassNotFoundException {
        super.getState(out);
        out.writeFloat(speed);
        out.writeObject(getSelfVelocity());
        out.writeFloat(health);

        for (Ability ability : abilities) {
            ability.getState(out);
        }
    }

    @Override
    public void setState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.setState(in);
        speed = in.readFloat();
        setSelfVelocity((Vector2) in.readObject());
        health = in.readFloat();

        for (Ability ability : abilities) {
            ability.setState(in);
        }
    }

    public int getAbilityNum(Ability ability) {
        for (int i = 0; i < abilities.size(); i++) {
            if (abilities.get(i) == ability) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public void damage(float damage) {
        health -= damage;
        if (health <= 0) {
            kill();
        }
    }

    @Override
    public float getHealth() { return health; }

    public float getMaxHealth() { return maxHealth; }


    public void setSelfVelocity(Vector2 newVelocity) {
        if (Utils.isZeroVec(newVelocity)) {
            // on stop walking
            if (!Utils.isZeroVec(selfVelocity)) {
                animation.ifPresent(controller -> controller.setAnimation(UnitAnimations.STAY, -1));
                animationSpeed = 1f;
            }
        } else {
            // on start walking
            if (Utils.isZeroVec(selfVelocity)) {
                animation.ifPresent(controller -> controller.setAnimation(UnitAnimations.WALK, -1));
            }

            animationSpeed = newVelocity.len() * UnitAnimations.WALK_ANIM_SPEED_FACTOR;
            setDirection(newVelocity);
        }

        selfVelocity.set(newVelocity.x, newVelocity.y);
    }

    public void setSelfVelocity(float x, float y) { setSelfVelocity(new Vector2(x, y)); }


    /** Definition for units. Loads abilities. */
    public static abstract class Definition extends DynamicObject.Definition {

        private static final long serialVersionUID = 1000000000L;

        public static final float DEFAULT_SPEED = 8f;

        public float speed = DEFAULT_SPEED;
        public Ability[] abilities;
        public float maxHealth;

        public Definition(String modelPath, Creator<Shape> shapeCreator, Ability[] abilities, float maxHealth) {
            super(modelPath, shapeCreator);
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
        public void doneLoading() {
            super.doneLoading();
            for (Ability ability : abilities) {
                ability.doneLoading(context);
            }
        }
    }
}
