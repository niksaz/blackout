package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.ingameui.settings.AbilityIconSettings;
import ru.spbau.blackout.ingameui.settings.IngameUISettings;
import ru.spbau.blackout.network.AbstractServer;
import ru.spbau.blackout.units.Vpx;

import static ru.spbau.blackout.BlackoutGame.getWorldHeight;
import static ru.spbau.blackout.BlackoutGame.getWorldWidth;
import static ru.spbau.blackout.java8features.Functional.foreach;


/**
 * Main class for in-game user interface.
 */
public class IngameUI {
    private final Stage stage;
    private final Array<IngameUIObject> uiObjects = new Array<>();

    /**
     * Creates all UI elements and sets itself as input processor.
     */
    public IngameUI(AbstractServer server, IngameUISettings settings) {
        Camera camera = new OrthographicCamera(getWorldWidth(), getWorldHeight());
        Viewport viewport = new StretchViewport(getWorldWidth(), getWorldHeight(), camera);
        this.stage = new Stage(viewport, BlackoutGame.get().spriteBatch());

        Gdx.input.setInputProcessor(this.stage);

        this.uiObjects.add(new ru.spbau.blackout.ingameui.objects.Stick(server, settings.stickSettings));
        for (AbilityIconSettings iconSettings : settings.abilities) {
            this.uiObjects.add(new ru.spbau.blackout.ingameui.objects.AbilityIcon(server, iconSettings));
        }
    }

    /** Load necessary assets. */
    public void load(GameContext context) {
        foreach(uiObjects, object -> object.load(context));
    }

    /** When assets are loaded. */
    public void doneLoading(GameContext context, Character character) {
        foreach(uiObjects, object -> object.doneLoading(context, stage, character));
    }

    /** Update for each frame. */
    public void update(float deltaTime) {
        this.stage.act(deltaTime);
        foreach(uiObjects, object -> object.update(deltaTime));
    }

    /** Called from GameScreen::draw() */
    public void draw() {
        stage.draw();
    }
}
