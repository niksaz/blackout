package ru.spbau.blackout.utils;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;


import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.java8features.Optional;

import static com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader.ParticleEffectLoadParameter;


public final class Particles {
    private Particles() {}

    // for lazy initialization
    private static final class ConstHolder {
        private ConstHolder() {}
        public static final ParticleEffectLoadParameter LOAD_PARAMETER
                = new ParticleEffectLoadParameter(BlackoutGame.get().particleSystem().getBatches());
    }
    private static ParticleEffectLoadParameter getParam() {
        return ConstHolder.LOAD_PARAMETER;
    }


    public static void load(String path, GameContext context) {
        context.assets().ifPresent(assets -> assets.load(path, ParticleEffect.class, getParam()));
    }

    /**
     * Returns the original of the particle effect.
     * One must not call any modifying methods like <code>init</code> and <code>translate</code>.
     */
    public static Optional<ParticleEffect> getOriginal(String path, GameContext context) {
        return context.assets().map(assets -> assets.get(path, ParticleEffect.class));
    }

    /**
     * Returns a copy of the particle effect.
     * One must <code>dispose</code> after usage.
     */
    public static Optional<ParticleEffect> getCopy(String path, GameContext context) {
        return context.assets().map(assets -> assets.get(path, ParticleEffect.class).copy());
    }
}
