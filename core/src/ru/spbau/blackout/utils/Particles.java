package ru.spbau.blackout.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;

import javax.swing.text.html.Option;

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
     * Returns a copy of the particle effect. One must dispose it by themselves.
     */
    public static Optional<ParticleEffect> get(String path, GameContext context) {
        return context.assets().map(assets -> assets.get(path, ParticleEffect.class).copy());
    }
}
