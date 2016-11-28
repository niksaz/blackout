package ru.spbau.blackout;

import com.badlogic.gdx.assets.AssetManager;

import java.util.Optional;

import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.network.AbstractServer;


/**
 * Provides some access to environment.
 * Used in functions like <code>load</code> and <code>doneLoading</code>.
 * Some getters return optional values. It means that those values don't exist on server.
 */
public interface GameContext {
    Optional<AssetManager> assets();
    GameWorld gameWorld();
    AbstractServer server();
    Optional<Hero> character();
}
