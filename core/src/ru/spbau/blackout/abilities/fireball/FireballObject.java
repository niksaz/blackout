package ru.spbau.blackout.abilities.fireball;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.physics.box2d.Shape;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.Damageable;
import ru.spbau.blackout.entities.DynamicObject;
import ru.spbau.blackout.graphiceffects.ParticleGraphicEffect;
import ru.spbau.blackout.entities.AbilityObject;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.shapescreators.CircleCreator;
import ru.spbau.blackout.specialeffects.ParticleSpecialEffect;
import ru.spbau.blackout.utils.Particles;
import org.jetbrains.annotations.Nullable;

import static ru.spbau.blackout.abilities.fireball.FireballAbility.CAST_SOUND_PATH;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.EXPLOSION_EFFECT_PATH;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.FIRE_EFFECT_PATH;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.IMPULSE_FACTOR;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.SHELL_MASS;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.SHELL_RADIUS;
import static ru.spbau.blackout.settings.GameSettings.SOUND_MAX_VOLUME;


public final class FireballObject extends AbilityObject {

    private float timeRest;
    private boolean shouldExplode = false;


    protected FireballObject(FireballObject.Definition def, long uid, float x, float y) {
        super(def, uid, x, y);

        timeRest = def.timeToLive;

        if (def.fireEffect != null) {
            graphicEffects.add(new ParticleGraphicEffect(getDef().getContext(), this, def.fireEffect.copy()));
        }
    }

    @Override
    public void beginContact(GameObject object) {
        super.beginContact(object);

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
        timeRest -= delta;
        if (timeRest <= 0) {
            kill();
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
    public static class Definition extends AbilityObject.Definition {

        private static final long serialVersionUID = 1000000000L;

        public float timeToLive;
        public float damage;
        @Nullable
        private /*final*/ transient ParticleEffect fireEffect;
        @Nullable
        private /*final*/ transient ParticleEffect explosionEffect;


        public Definition() {
            super(null, new CircleCreator(SHELL_RADIUS));
            this.mass = SHELL_MASS;
        }

        @Override
        public void load(GameContext context) {
            super.load(context);
            Particles.load(context, FIRE_EFFECT_PATH);
            Particles.load(context, EXPLOSION_EFFECT_PATH);
            context.getAssets().load(CAST_SOUND_PATH, Sound.class);
        }

        @Override
        public void doneLoading() {
            super.doneLoading();
            fireEffect = Particles.getOriginal(getContext(), FIRE_EFFECT_PATH);
            explosionEffect = Particles.getOriginal(getContext(), EXPLOSION_EFFECT_PATH);
        }

        @Override
        public GameObject makeInstance(long uid, float x, float y) {
            return new FireballObject(this, uid, x, y);
        }

        @Override
        protected String castSoundPath() {
            return CAST_SOUND_PATH;
        }
    }
}
