package ru.spbau.blackout.abilities.fireball;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.physics.box2d.Shape;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.Damageable;
import ru.spbau.blackout.graphiceffects.ParticleGraphicEffect;
import ru.spbau.blackout.entities.AbilityObject;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.java8features.Optional;
import ru.spbau.blackout.specialeffects.ParticleSpecialEffect;
import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.utils.Particles;

import static ru.spbau.blackout.abilities.fireball.FireballAbility.EXPLOSION_EFFECT_PATH;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.FIRE_EFFECT_PATH;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.IMPULSE_FACTOR;


public final class FireballObject extends AbilityObject {

    private float timeRest;
    private final FireballObject.Definition def;
    private boolean shouldExplode = false;


    protected FireballObject(FireballObject.Definition def, long uid, float x, float y) {
        super(def, uid, x, y);
        this.def = def;

        timeRest = def.timeToLive;

        def.fireEffect.ifPresent(effect ->
            graphicEffects.add(new ParticleGraphicEffect(this, effect.copy()))
        );
    }


    @Override
    public void beginContact(GameObject object) {
        super.beginContact(object);

        if (object instanceof GameUnit) {
            GameUnit unit = (GameUnit) object;
            // This object is going to dye. So, we don't care about changes of its velocity.
            velocity.add(body.getLinearVelocity()).scl(IMPULSE_FACTOR);
            unit.applyImpulse(velocity);
        }

        if (object instanceof Damageable) {
            ((Damageable) object).damage(this.def.damage);
        }

        shouldExplode = true;

        this.kill();
    }

    @Override
    public void updateState(float delta) {
        super.updateState(delta);
        timeRest -= delta;
        if (timeRest <= 0) {
            this.kill();
        }
    }

    @Override
    public void getState(ObjectOutputStream out) throws IOException, ClassNotFoundException {
        super.getState(out);
        out.writeBoolean(shouldExplode);
    }

    @Override
    public void setState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.setState(in);
        shouldExplode = in.readBoolean();
    }

    @Override
    public void kill() {
        super.kill();
        // play explosion effect
        if (shouldExplode && def.explosionEffect.isPresent()) {
            ParticleSpecialEffect.create(def.explosionEffect.get().copy(), this.getChestPivot());
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
            this.fireEffect = Optional.of(Particles.getOriginal(context, FIRE_EFFECT_PATH));
            this.explosionEffect = Optional.of(Particles.getOriginal(context, EXPLOSION_EFFECT_PATH));
        }

        @Override
        public void initializeWithoutUi(GameContext context) {
            super.initializeWithoutUi(context);
            fireEffect = Optional.empty();
            explosionEffect = Optional.empty();
        }

        @Override
        public GameObject makeInstance(long uid, float x, float y) {
            return new FireballObject(this, uid, x, y);
        }
    }
}
