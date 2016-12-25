package ru.spbau.blackout.abilities.fireball;

import com.badlogic.gdx.audio.Sound;
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
import ru.spbau.blackout.specialeffects.ParticleSpecialEffect;
import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.utils.Particles;
import org.jetbrains.annotations.Nullable;

import static ru.spbau.blackout.settings.GameSettings.SOUND_MAX_VOLUME;


public final class FireballObject extends AbilityObject {

    private static final String CAST_SOUND_PATH = "sounds/fire.ogg";
    private static final float IMPULSE_FACTOR = 40f;

    private static final String FIRE_EFFECT_PATH = "abilities/fireball/particles/fireball.pfx";
    private static final String EXPLOSION_EFFECT_PATH = "effects/small_explosion/small_explosion.pfx";


    private float timeRest;
    private boolean shouldExplode = false;


    protected FireballObject(FireballObject.Definition def, long uid, float x, float y) {
        super(def, uid, x, y);

        timeRest = def.timeToLive;

        if (def.fireEffect != null) {
            graphicEffects.add(new ParticleGraphicEffect(this, def.fireEffect.copy()));
        }

        if (def.castSound != null) {
            def.castSound.play(getDef().getContext().getSettings().soundVolume * SOUND_MAX_VOLUME);
        }
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
            ((Damageable) object).damage(((Definition) getDef()).damage);
        }

        shouldExplode = true;

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
            ParticleSpecialEffect.create(explosionEffect.copy(), getChestPivot());
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
        @Nullable private /*final*/ transient ParticleEffect fireEffect;
        @Nullable private /*final*/ transient ParticleEffect explosionEffect;
        @Nullable private /*final*/ transient Sound castSound;


        public Definition(String modelPath, Creator<Shape> shapeCreator, float mass) {
            super(modelPath, shapeCreator, mass);
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
            fireEffect = Particles.getOriginal(context, FIRE_EFFECT_PATH);
            explosionEffect = Particles.getOriginal(context, EXPLOSION_EFFECT_PATH);
            castSound = context.getAssets().get(CAST_SOUND_PATH, Sound.class);
        }

        @Override
        public GameObject makeInstance(long uid, float x, float y) {
            return new FireballObject(this, uid, x, y);
        }
    }
}
