package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Stage;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.Character;


/**
 * Abstract class for all elements of in-game user interface.
 */
public abstract class IngameUIObject {
    /** Load assets. */
    public abstract void load(GameContext context);
    public abstract void doneLoading(GameContext context, Stage stage);
    /** Update for each frame. */
    public abstract void update(float deltaTime);
    public abstract void dispose();
}
