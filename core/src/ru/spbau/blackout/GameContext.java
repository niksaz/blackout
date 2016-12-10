package ru.spbau.blackout;

import com.badlogic.gdx.assets.AssetManager;

import ru.spbau.blackout.java8features.Optional;
import ru.spbau.blackout.settings.GameSettings;
import ru.spbau.blackout.worlds.GameWorld;


/**
 * Provides some access to environment.
 * Used in functions like <code>load</code> and <code>initialize</code>.
 * Some getters return optional values. It means that those values don't exist on server.
 */
public interface GameContext {
    // TODO: comments
    boolean hasIO();
    /*FIXME: use annotation nullable*/
    AssetManager getAssets();
    GameWorld gameWorld();
    /*FIXME: nullable*/
    GameSettings getSettings();

    // FIXME: default doesn't work
    Optional<AssetManager> assets();
    Optional<GameSettings> settings();
}
