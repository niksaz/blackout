package ru.spbau.blackout.abilities.fireball;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.AbilityObject;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.graphic_effects.ParticleGraphicEffect;
import ru.spbau.blackout.java8features.Optional;
import ru.spbau.blackout.special_effects.ParticleSpecialEffect;
import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.utils.Particles;

import static ru.spbau.blackout.abilities.fireball.FireballAbility.EXPLOSION_EFFECT_PATH;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.FIRE_EFFECT_PATH;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.IMPULSE_FACTOR;


public final class FireballObject extends AbilityObject {
    private float timeRest;
    private final FireballObject.Definition def;


    protected FireballObject(FireballObject.Definition def, long uid, float x, float y) {
        super(def, uid, x, y);
        this.def = def;

        this.timeRest = def.timeToLive;

        def.fireEffect.ifPresent(effect -> {
            this.graphicEffects.add(new ParticleGraphicEffect(this, effect.copy()));
        });
    }


    @Override
    public void beginContact(GameObject object) {
        super.beginContact(object);

        if (object instanceof GameUnit) {
            GameUnit unit = (GameUnit) object;
            // This object is going to dye. So, we don't care about changes of its velocity.
            this.velocity.add(this.body.getLinearVelocity()).scl(IMPULSE_FACTOR);
            unit.applyImpulse(this.velocity);
        }

//        if (object instanceof Damageable) {
//            ((Damageable) object).damage(this.def.damage);
//        }
        if (object instanceof GameUnit) {
            ((GameUnit) object).damage(this.def.damage);
        }

        // play explosion effect
        this.def.explosionEffect.ifPresent(effect -> {
            ParticleSpecialEffect.create(effect.copy(), this.getChestPivot());
        });

        this.kill();
    }

    @Override
    public void updateState(float deltaTime) {
        super.updateState(deltaTime);
        timeRest -= deltaTime;
        if (timeRest <= 0) {
            this.kill();
        }
    }

    @Override
    public void updateForSecondStep() {
        super.updateForSecondStep();
    }

    /**
     * Additionally defines timeToLive for an object.
     */
    public static class Definition extends AbilityObject.Definition {

        private static final long serialVersionUID = 1000000000L;

        public float timeToLive;
        public float damage;
        private /*final*/ transient Optional<ParticleEffect> fireEffect;
        private /*final*/ transient Optional<ParticleEffect> explosionEffect;


        public Definition(String modelPath, Creator<Shape> shapeCreator, float mass) {
            super(modelPath, shapeCreator, mass);
        }

        @Override
        public void load(GameContext context) {
            super.load(context);
            Particles.load(context, FIRE_EFFECT_PATH);
            Particles.load(context, EXPLOSION_EFFECT_PATH);
        }

        @Override
        public void doneLoading() {
            super.doneLoading();
            this.fireEffect = Particles.getOriginal(context, FIRE_EFFECT_PATH);
            this.explosionEffect = Particles.getOriginal(context, EXPLOSION_EFFECT_PATH);
        }

        @Override
        public GameObject makeInstance(long uid, float x, float y) {
            return new FireballObject(this, uid, x, y);
        }
    }
}
