package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.ingameui.objects.AbilityIcon;
import ru.spbau.blackout.ingameui.objects.HealthBar;
import ru.spbau.blackout.ingameui.objects.Stick;
import ru.spbau.blackout.network.UIServer;

import static ru.spbau.blackout.BlackoutGame.getWorldHeight;
import static ru.spbau.blackout.BlackoutGame.getWorldWidth;
import static ru.spbau.blackout.java8features.Functional.foreach;


/**
 * Main class for in-game user interface.
 */
public class IngameUI {

    // FIXME: calculate positions with regard to aspect ratio
    private static final Vector2[] ABILITY_ICONS_POS = {
            new Vector2(1100, 450),
            new Vector2(1100, 300),
            new Vector2(1100, 150)
    };

    public final Stage stage;
    private final Array<IngameUIObject> uiObjects = new Array<>();

    /**
     * Creates all UI elements and sets itself as input processor.
     */
    public IngameUI(UIServer server) {
        Camera camera = new OrthographicCamera(getWorldWidth(), getWorldHeight());
        Viewport viewport = new StretchViewport(getWorldWidth(), getWorldHeight(), camera);
        this.stage = new Stage(viewport, BlackoutGame.get().spriteBatch());

        Gdx.input.setInputProcessor(this.stage);

        this.uiObjects.add(new Stick(server));

        for (int i = 0; i < ABILITY_ICONS_POS.length; i++) {
            this.uiObjects.add(new AbilityIcon(server, i, ABILITY_ICONS_POS[i]));
        }

        this.uiObjects.add(new HealthBar());
    }

    /**
     * Load necessary assets.
     */
    public void load(AssetManager assets) {
        foreach(uiObjects, object -> object.load(assets));
    }

    /**
     * When assets are loaded.
     */
    public void doneLoading(AssetManager assets, Character character) {
        foreach(uiObjects, object -> object.doneLoading(assets, stage, character));
    }

    /**
     * Update for each frame.
     */
    public void update(float deltaTime) {
        this.stage.act(deltaTime);
        foreach(uiObjects, object -> object.update(deltaTime));
    }

    /**
     * Called from GameScreen::draw()
     */
    public void draw() {
        stage.draw();
    }

    public void dispose() {
         stage.dispose();
         foreach(uiObjects, IngameUIObject::dispose);
    }
}
