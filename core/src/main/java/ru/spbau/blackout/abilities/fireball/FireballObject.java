package ru.spbau.blackout.abilities.fireball;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.Damageable;
import ru.spbau.blackout.entities.DynamicObject;
import ru.spbau.blackout.graphiceffects.ParticleGraphicEffect;
import ru.spbau.blackout.abilities.DynamicAbilityObject;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.serializationutils.EffectiveInputStream;
import ru.spbau.blackout.serializationutils.EffectiveOutputStream;
import ru.spbau.blackout.shapescreators.CircleCreator;
import ru.spbau.blackout.specialeffects.ParticleSpecialEffect;
import ru.spbau.blackout.utils.Particles;
import ru.spbau.blackout.utils.Uid;

import org.jetbrains.annotations.Nullable;

import static ru.spbau.blackout.abilities.fireball.FireballAbility.CAST_SOUND_PATH;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.EXPLOSION_EFFECT_PATH;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.FIRE_EFFECT_PATH;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.IMPULSE_FACTOR;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.SHELL_MASS;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.SHELL_RADIUS;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.TIME_TO_LIVE;


public final class FireballObject extends DynamicAbilityObject {

    private float timeToLive;
    private boolean shouldExplode = false;


    protected FireballObject(FireballObject.Definition def, Uid uid, float x, float y) {
        super(def, uid, x, y);

        timeToLive = def.timeToLive;

        if (def.fireEffect != null) {
            graphicEffects.add(new ParticleGraphicEffect(getDef().getContext(), this, def.fireEffect.copy()));
        }
    }

    @Override
    public void beginContact(GameObject object) {
        shouldExplode = true;

        if (object instanceof DynamicObject) {
            DynamicObject dynamic = (DynamicObject) object;
            // This object is going to dye. So, we don't care about changes of its velocity.
            velocity.add(body.getLinearVelocity()).scl(IMPULSE_FACTOR);
            dynamic.applyImpulse(velocity);
        }

        if (object instanceof Damageable) {
            ((Damageable) object).damage(((Definition) getDef()).damage);
        }

        kill();
    }

    @Override
    public void updateState(float delta) {
        super.updateState(delta);
        timeToLive -= delta;
        if (timeToLive <= 0) {
            kill();
        }
    }

    @Override
    public void getState(EffectiveOutputStream out) throws IOException {
        super.getState(out);
        out.writeBoolean(shouldExplode);
    }

    @Override
    public void setState(EffectiveInputStream in) throws IOException {
        super.setState(in);
        shouldExplode = in.readBoolean();
    }

    @Override
    public void kill() {
        super.kill();
        // play explosion effect
        ParticleEffect explosionEffect = ((Definition) getDef()).explosionEffect;
        if (shouldExplode && explosionEffect != null) {
            ParticleSpecialEffect.create(getDef().getContext(), explosionEffect.copy(), getChestPivot());
        }
    }

    @Override
    public void updateForSecondStep() {
        super.updateForSecondStep();
    }

    /**
     * Additionally defines timeToLive for an object.
     */
    public static class Definition extends DynamicAbilityObject.Definition {

        private static final long serialVersionUID = 1000000000L;

        public float timeToLive;
        public float damage;
        @Nullable
        private /*final*/ transient ParticleEffect fireEffect;
        @Nullable
        private /*final*/ transient ParticleEffect explosionEffect;


        public Definition() {
            super(null, new CircleCreator(SHELL_RADIUS), null, CAST_SOUND_PATH);
            mass = SHELL_MASS;
            chestPivotOffset.set(0, 0, 1.5f);
            isSensor = true;
            timeToLive = TIME_TO_LIVE;
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
            fireEffect = Particles.getOriginal(getContext(), FIRE_EFFECT_PATH);
            explosionEffect = Particles.getOriginal(getContext(), EXPLOSION_EFFECT_PATH);
        }

        @Override
        public GameObject makeInstance(Uid uid, float x, float y) {
            return new FireballObject(this, uid, x, y);
        }
    }
}
