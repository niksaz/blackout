package ru.spbau.blackout.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;


import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.java8features.Optional;

import static com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader.ParticleEffectLoadParameter;


public final class Particles {
    private Particles() {}

    public static void load(String path) {
        ParticleEffectLoadParameter param =
                new ParticleEffectLoadParameter(BlackoutGame.get().particleSystem().getBatches());
        BlackoutGame.get().context().getAssets().load(path, ParticleEffect.class, param);
    }

    /**
     * Returns the original of the particle effect.
     * One must not call any modifying methods like <code>init</code> and <code>translate</code>.
     */
    public static Optional<ParticleEffect> getOriginal(String path) {
        return BlackoutGame.get().context().assets().map(assets -> assets.get(path, ParticleEffect.class));
    }

    /**
     * Returns a copy of the particle effect.
     * One must <code>dispose</code> after usage.
     */
    public static Optional<ParticleEffect> getCopy(String path) {
        return BlackoutGame.get().context().assets().map(assets -> assets.get(path, ParticleEffect.class).copy());
    }
}