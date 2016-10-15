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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.Physics;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.ingameui.IngameUI;
import ru.spbau.blackout.rooms.GameRoom;

public class GameScreen extends BlackoutScreen {
    public static final float DEFAULT_CAMERA_X_OFFSET = 0;
    public static final float DEFAULT_CAMERA_Y_OFFSET = 2;
    public static final float DEFAULT_CAMERA_HEIGHT = 20;

    private ModelInstance map;
    private GameRoom room;

    private PerspectiveCamera camera;
    private Array<GameObject> units;
    private Hero hero;
    private IngameUI ui;

    // just for test
    public Environment environment;

    private AssetManager assets;
    private boolean loading;
    private final Physics physics;

    public GameScreen(BlackoutGame game, GameRoom room) {
        super(game);
        this.room = room;

        // getting information from room
        units = room.getObjects();
        hero = room.getHero();

        // initialize main camera
        camera = new PerspectiveCamera();
        camera.fieldOfView = 67;
        camera.near = 1f;
        camera.far = 30000f;

        // initialize environment
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 2f, 2f, 2f, 100f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0f, 0f, -1f));

        // initialize some other things
        physics = new Physics();
        ui = new IngameUI(this);
        assets = new AssetManager();
    }

    @Override
    public void show() {
        super.show();

        // start loading
        ui.load(assets);
        for (GameObject unit : units) {
            assets.load(unit.getModelPath(), Model.class);
        }
        assets.load(hero.getModelPath(), Model.class);
        assets.load(room.getMap(), Model.class);
        loading = true;
    }

    @Override
    public void render(float delta) {
        super.render(delta);

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

        game.modelBatch.begin(camera);
        for (GameObject unit : units) {
            game.modelBatch.render(unit.getModelInstance(), environment);
        }
        game.modelBatch.render(hero.getModelInstance(), environment);
        game.modelBatch.render(map, environment);
        game.modelBatch.end();

        ui.draw();

        physics.debugRender(camera);

        update(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        ui.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        assets.dispose();
    }

    public Hero getHero() {
        return hero;
    }

    private void doneLoadingForUnit(GameObject unit) {
        Model model = assets.get(unit.getModelPath(), Model.class);
        unit.makeInstance(model);
    }

    private void doneLoading() {
        for (GameObject unit : units) {
            doneLoadingForUnit(unit);
        }
        doneLoadingForUnit(hero);

        map = new ModelInstance(assets.get(room.getMap(), Model.class));
        ui.doneLoading(assets);

        loading = false;
    }

    private void update(final float delta) {
        for (GameObject unit : units) {
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

        physics.update(delta);
    }
}
