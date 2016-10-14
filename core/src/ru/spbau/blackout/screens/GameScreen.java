package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.HashSet;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.ingameui.IngameUI;
import ru.spbau.blackout.rooms.GameRoom;

public class GameScreen extends BlackoutScreen {
    public static final float DEFAULT_CAMERA_X_OFFSET = 0;
    public static final float DEFAULT_CAMERA_Y_OFFSET = 2;
    public static final float DEFAULT_CAMERA_HEIGHT = 18;
//    public static final float DEFAULT_CAMERA_HEIGHT = 5;

    private ModelInstance map;
    private GameRoom room;

    private PerspectiveCamera camera;
    private Array<GameUnit> units;
    private Hero hero;
    private IngameUI ui;

    // just for test
    public Environment environment;

    private AssetManager assets;
    private boolean loading;

    public GameScreen(BlackoutGame game, GameRoom room) {
        super(game);
        this.room = room;

        units = room.getUnits();
        hero = room.getHero();

        ui = new IngameUI(this);
    }

    @Override
    public void show() {
        // initialize main camera
        camera = new PerspectiveCamera();
        camera.fieldOfView = 67;
        camera.near = 1f;
        camera.far = 30000f;

        // initialize environment
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 2f, 2f, 2f, 100f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0f, 0f, -1f));

        // start loading
        assets = new AssetManager();
        for (GameUnit unit : units) {
            assets.load(unit.getModelPath(), Model.class);
        }
        assets.load(hero.getModelPath(), Model.class);
        assets.load(room.getMap(), Model.class);
        loading = true;
    }

    private void doneLoadingForUnit(GameUnit unit) {
        Model model = assets.get(unit.getModelPath(), Model.class);
        unit.makeInstance(model);
    }

    private void doneLoading() {
        for (GameUnit unit : units) {
            doneLoadingForUnit(unit);
        }
        doneLoadingForUnit(hero);

        map = new ModelInstance(assets.get(room.getMap(), Model.class));

        loading = false;
    }

    private void update(float delta) {
        for (GameUnit unit : units) {
            unit.update(delta);
        }
        hero.update(delta);

        Vector2 heroPos = hero.getPosition();
        camera.position.set(
                DEFAULT_CAMERA_X_OFFSET + heroPos.x,
                DEFAULT_CAMERA_HEIGHT + hero.getHeight(),
                DEFAULT_CAMERA_Y_OFFSET + heroPos.y);
        camera.lookAt(heroPos.x, hero.getHeight(), heroPos.y);
        camera.update();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (loading) {
            if (assets.update()) {
                doneLoading();
            }
            /*float progress = assets.getProgress();
            if(assets.isLoaded(LOADING_SCREEN)) {
            }*/
            // TODO: loading screen with progress bar
            return;
        }

        update(delta);

        game.modelBatch.begin(camera);
        for (GameUnit unit : units) {
            game.modelBatch.render(unit.getModelInstance(), environment);
        }
        game.modelBatch.render(hero.getModelInstance(), environment);
        game.modelBatch.render(map, environment);
        game.modelBatch.end();

        ui.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        ui.resize(width, height);
    }

    @Override
    public void dispose() {
        assets.dispose();
    }

    public Hero getHero() {
        return hero;
    }
}
