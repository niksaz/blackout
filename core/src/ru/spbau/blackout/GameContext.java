package ru.spbau.blackout;

import com.badlogic.gdx.assets.AssetManager;

import ru.spbau.blackout.java8features.Optional;


/**
 * Provides some access to environment.
 * Used in functions like <code>load</code> and <code>doneLoading</code>.
 * Some getters return optional values. It means that those values don't exist on server.
 */
public interface GameContext {
    Optional<AssetManager> assets();
    GameWorld gameWorld();
}
