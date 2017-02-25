package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.ArrayList;
import java.util.List;

import ru.spbau.blackout.GameContext;

import static ru.spbau.blackout.java8features.Functional.foreach;


/**
 * Abstract class for all elements of in-game user interface.
 */
public abstract class IngameUIObject {
    private final List<Actor> actors = new ArrayList<>();
    private final Stage stage;

    public IngameUIObject(Stage stage) {
        this.stage = stage;
    }

    protected final void addActor(Actor actor) {
        stage.addActor(actor);
        actors.add(actor);
    }

    /** Load assets. */
    public abstract void load(GameContext context);

    public abstract void doneLoading(GameContext context);

    /** Update for each frame. */
    public abstract void update(float delta);

    public void dispose() {
    }

    public final void remove() {
        foreach(actors, Actor::remove);
    }

    public Stage getStage() {
        return stage;
    }
}
