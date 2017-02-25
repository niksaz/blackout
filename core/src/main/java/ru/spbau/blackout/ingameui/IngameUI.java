package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import ru.spbau.blackout.GameContext;

import static ru.spbau.blackout.java8features.Functional.foreach;

public abstract class IngameUI {

    private final Stage stage;
    private final Array<IngameUIObject> uiObjects = new Array<>();

    public IngameUI(Stage stage) {
        this.stage = stage;
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

    public void dispose() {
        for (IngameUIObject uio : uiObjects) {
            uio.remove();
            uio.dispose();
        }
    }

    /**
     * Called from GameScreen::draw()
     */
    public void draw() {
        stage.draw();
    }

    public void addUiObject(IngameUIObject uiObject) {
        this.uiObjects.add(uiObject);
    }

    public Stage getStage() {
        return stage;
    }
}
