package ru.spbau.blackout;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.settings.GameSettings;
import ru.spbau.blackout.worlds.GameWorld;


/**
 * Provides some access to environment.
 * Used in functions like <code>load</code> and <code>initializeGameWorld</code>.
 * Some getters return optional values. It means that those values don't exist on server.
 */
public interface GameContext {
    GameWorld gameWorld();

    boolean hasUI();
    AssetManager getAssets();
    GameSettings getSettings();
    GameScreen getScreen();
    ParticleSystem getParticleSystem();
}
