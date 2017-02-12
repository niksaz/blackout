package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.GameContext;

import static ru.spbau.blackout.BlackoutGame.getWorldHeight;
import static ru.spbau.blackout.BlackoutGame.getWorldWidth;
import static ru.spbau.blackout.java8features.Functional.foreach;

public abstract class IngameUI {

    private final Stage stage;
    private final Array<IngameUIObject> uiObjects = new Array<>();

    public IngameUI() {
        Camera camera = new OrthographicCamera(getWorldWidth(), getWorldHeight());
        Viewport viewport = new StretchViewport(getWorldWidth(), getWorldHeight(), camera);
        stage = new Stage(viewport, BlackoutGame.get().spriteBatch());
        Gdx.input.setInputProcessor(getStage());
    }

    public IngameUI(IngameUI previous) {
        this();
        foreach(previous.uiObjects, IngameUIObject::removeFromStage);
        foreach(previous.stage.getActors(), this::addActor);
        previous.stage.getActors().clear();
    }

    /**
     * Load necessary assets.
     */
    public void load(GameContext context) {
        foreach(uiObjects, object -> object.load(context));
    }

    /**
     * When assets are loaded.
     */
    public void doneLoading(GameContext context) {
        foreach(uiObjects, object -> object.doneLoading(context));
    }

    /**
     * Update for each frame.
     */
    public void update(float delta) {
        stage.act(delta);
        foreach(uiObjects, object -> object.update(delta));
    }

    public void addActor(Actor actor) {
        stage.addActor(actor);
    }

    /**
     * Called from GameScreen::draw()
     */
    public void draw() {
        stage.draw();
    }

    public void dispose() {
        foreach(uiObjects, IngameUIObject::dispose);
        stage.dispose();
    }

    public void addUiObject(IngameUIObject uiObject) {
        this.uiObjects.add(uiObject);
    }

    public Stage getStage() {
        return stage;
    }
}
