package ru.spbau.blackout.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.GameContext;

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


    public static void load(String path, AssetManager assets) {
        assets.load(path, ParticleEffect.class, getParam());
    }

    public static ParticleEffect get(String path, AssetManager assets) {
        return assets.get(path, ParticleEffect.class).copy();
    }
}
