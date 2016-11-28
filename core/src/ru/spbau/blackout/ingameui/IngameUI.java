package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.ingameui.settings.AbilityIconSettings;
import ru.spbau.blackout.ingameui.settings.IngameUISettings;
import ru.spbau.blackout.screens.GameScreen;


/**
 * Main class for in-game user interface.
 */
public class IngameUI {
    private final Stage stage;
    private final Array<IngameUIObject> uiObjects = new Array<>();

    /**
     * Creates all UI elements and sets itself as input processor.
     */
    public IngameUI(GameScreen screen, IngameUISettings settings) {
        Camera camera = new OrthographicCamera();
        this.stage = new Stage(new ScreenViewport(camera), BlackoutGame.get().spriteBatch());

        Gdx.input.setInputProcessor(this.stage);

        this.uiObjects.add(new ru.spbau.blackout.ingameui.objects.Stick(screen.getServer(), settings.stickSettings));
        for (AbilityIconSettings iconSettings : settings.abilities) {
            this.uiObjects.add(new ru.spbau.blackout.ingameui.objects.AbilityIcon(screen.getServer(), iconSettings));
        }
    }

    /** Load necessary assets. */
    public void load(AssetManager assets) {
        for (IngameUIObject object : uiObjects) {
            object.load(assets);
        }
    }

    /** When assets are loaded. */
    public void doneLoading(AssetManager assets, Hero character) {
        for (IngameUIObject object : uiObjects) {
            object.doneLoading(assets, stage, character);
        }
    }

    /** Update for each frame. */
    public void update(float deltaTime) {
        for (IngameUIObject object : uiObjects) {
            object.update(deltaTime);
        }
    }

    /** On window resize */
    public void resize(int width, int height) {
        // TODO: resize each UI object
        stage.getViewport().update(width, height);
    }

    /** Called from GameScreen::draw() */
    public void draw() {
        stage.draw();
    }
}
