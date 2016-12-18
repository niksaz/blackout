package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Stage;

import ru.spbau.blackout.entities.Character;


/**
 * Abstract class for all elements of in-game user interface.
 */
public abstract class IngameUIObject {
    /** Load assets. */
    public abstract void load(AssetManager assets);
    /** When assets are loaded. */
    public abstract void doneLoading(AssetManager assets, Stage stage, Character character);
    /** Update for each frame. */
    public abstract void update(float deltaTime);
}
