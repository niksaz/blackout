package ru.spbau.blackout.abilities.simpleshell;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.graphiceffects.ParticleGraphicEffect;
import ru.spbau.blackout.abilities.DynamicAbilityObject;
import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.utils.Particles;
import ru.spbau.blackout.utils.Uid;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;


public abstract class SimpleShellObject extends DynamicAbilityObject {

    private float timeToLive;

    protected SimpleShellObject(Definition def, Uid uid, float x, float y) {
        super(def, uid, x, y);

        timeToLive = def.timeToLive();

        if (def.liveEffect != null) {
            ParticleGraphicEffect.create(this, def.liveEffect.copy(), getDef().getContext());
        }
    }

    @Override
    public void updateState(float delta) {
        super.updateState(delta);
        timeToLive -= delta;
        if (timeToLive <= 0) {
            kill();
        }
    }

    /**
     * Additionally defines timeToLive for an object.
     */
    public static abstract class Definition extends DynamicAbilityObject.Definition {

        private static final long serialVersionUID = 1000000000L;

        @Nullable
        private /*final*/ transient ParticleEffect liveEffect;
        public float damage;

        public Definition(@Nullable String modelPath, @Nullable Creator<Shape> shapeCreator,
                          @Nullable String deathEffect, @Nullable String castSoundPath, float mass) {
            super(modelPath, shapeCreator, deathEffect, castSoundPath);
            this.mass = mass;
            chestPivotOffset.set(0, 0, 1.5f);
            isSensor = true;
        }

        @Override
        public void load(GameContext context) {
            super.load(context);
            Particles.load(context, liveEffectPath());
        }

        @Override
        public void doneLoading() {
            super.doneLoading();
            liveEffect = Particles.getOriginal(getContext(), liveEffectPath());
        }

        @Contract(pure = true)
        protected abstract String liveEffectPath();
        @Contract(pure = true)
        protected abstract String explosionEffectPath();
        @Contract(pure = true)
        protected abstract float timeToLive();
    }
}
