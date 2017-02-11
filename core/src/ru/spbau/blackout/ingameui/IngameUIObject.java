package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ru.spbau.blackout.GameContext;


/**
 * Abstract class for all elements of in-game user interface.
 */
public abstract class IngameUIObject {
    private final Set<Actor> actors = new HashSet<>();
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

    public final void removeFromStage() {
        for (Iterator<Actor> it = stage.getActors().iterator(); it.hasNext();) {
            if (actors.contains(it.next())) {
                it.remove();
            }
        }
    }

    public Stage getStage() {
        return stage;
    }
}
