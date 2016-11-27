package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.network.AbstractServer;

public abstract class IngameUIObject {
    /** server to send UI events */
    protected final AbstractServer server;

    public IngameUIObject(AbstractServer server) {
        this.server = server;
    }

    public abstract void load(AssetManager assets);
    public abstract void doneLoading(AssetManager assets, Stage stage, Hero hero);
    public abstract void update(float deltaTime);
}
