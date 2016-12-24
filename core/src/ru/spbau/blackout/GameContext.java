package ru.spbau.blackout;

import com.badlogic.gdx.assets.AssetManager;

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

    // TODO: comments
    boolean hasUI();
    /*FIXME: use annotation nullable*/
    AssetManager getAssets();
    /*FIXME: nullable*/
    GameSettings getSettings();
    /*FIXME: nullable*/
    GameScreen getScreen();
}
