package ru.spbau.blackout.utils;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.GameContext;

import static com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader.ParticleEffectLoadParameter;


public final class Particles {
    private Particles() {}

    public static void load(GameContext context, String path) {
        ParticleEffectLoadParameter param =
                new ParticleEffectLoadParameter(context.getParticleSystem().getBatches());
        context.getAssets().load(path, ParticleEffect.class, param);
    }

    /**
     * Returns the original of the particle effect.
     * One must not call any modifying methods like <code>init</code> and <code>translate</code>.
     */
    public static ParticleEffect getOriginal(GameContext context, String path) {
        return context.getAssets().get(path, ParticleEffect.class);
    }

    /**
     * Returns a copy of the particle effect.
     * One must <code>dispose</code> after usage.
     */
    public static ParticleEffect getCopy(GameContext context, String path) {
        return context.getAssets().get(path, ParticleEffect.class).copy();
    }
}
