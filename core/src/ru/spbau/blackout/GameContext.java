package ru.spbau.blackout;

import com.badlogic.gdx.assets.AssetManager;

import ru.spbau.blackout.java8features.Optional;


/**
 * Provides some access to environment.
 * Used in functions like <code>load</code> and <code>initialize</code>.
 * Some getters return optional values. It means that those values don't exist on server.
 */
public interface GameContext {
    // TODO: comments
    boolean hasGraphics();
    /*FIXME: use annotation nullable*/ AssetManager getAssets();
    GameWorld gameWorld();

    default Optional<AssetManager> assets() {
        if (this.hasGraphics()) {
            return Optional.of(this.getAssets());
        } else {
            return Optional.empty();
        }
    }
}
