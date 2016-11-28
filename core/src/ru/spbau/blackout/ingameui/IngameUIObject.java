package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Stage;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.network.AbstractServer;


/**
 * Abstract class for all elements of in-game user interface.
 */
public abstract class IngameUIObject {
    /** server to send UI events */
    protected final AbstractServer server;

    public IngameUIObject(AbstractServer server) {
        this.server = server;
    }

    /** Load assets. */
    public abstract void load(GameContext context);
    /** When assets are loaded. */
    public abstract void doneLoading(GameContext context, Stage stage, Hero hero);
    /** Update for each frame. */
    public abstract void update(float deltaTime);
}
