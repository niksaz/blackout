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

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.Physics;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.ingameui.IngameUI;
import ru.spbau.blackout.rooms.GameRoom;
import ru.spbau.blackout.utils.ScreenManager;

public class GameScreen extends BlackoutScreen {
    public static final float DEFAULT_CAMERA_X_OFFSET = 0;
    public static final float DEFAULT_CAMERA_Y_OFFSET = 2;
    public static final float DEFAULT_CAMERA_HEIGHT = 20;

    private ModelInstance map;

    private PerspectiveCamera camera;
    private final Array<GameObject> gameObjects = new Array<>();
    private Hero character;
    private final IngameUI ui = new IngameUI(this);

    public Environment environment;
    private final Physics physics = new Physics();

    // should be here, not in loading screen because it owns all assets
    private final AssetManager assets = new AssetManager();

    // nullable
    private LoadingScreen loadingScreen;

    public GameScreen(BlackoutGame game, GameRoom room) {
        super(game);

        loadingScreen = new LoadingScreen(game, room);

        // initialize main camera
        camera = new PerspectiveCamera();
        camera.fieldOfView = 67;
        camera.near = 1f;
        camera.far = 30000f;

        // initialize environment
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 2f, 2f, 2f, 100f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0f, 0f, -1f));
    }

    @Override
    public void show() {
        super.show();

        // if not loaded yet
        if (loadingScreen != null) {
            ScreenManager.getInstance().setScreen(loadingScreen);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        game.modelBatch.begin(camera);
        for (GameObject object : gameObjects) {
            game.modelBatch.render(object.getModelInstance(), environment);
        }
        game.modelBatch.render(map, environment);
        game.modelBatch.end();

        ui.draw();

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
    }

    public Hero getCharacter() {
        return character;
    }

    /**
     * Updates game world on every frame.
     */
    private void update(final float delta) {
        for (GameObject object : gameObjects) {
            object.update(delta);
        }

        Vector2 charPos = character.getPosition();
        camera.position.set(
                DEFAULT_CAMERA_X_OFFSET + charPos.x,
                DEFAULT_CAMERA_HEIGHT + character.getHeight(),
                DEFAULT_CAMERA_Y_OFFSET + charPos.y);
        camera.lookAt(charPos.x, character.getHeight(), charPos.y);
        camera.update();

        physics.update(delta);
    }

    private void doneLoading() {
        loadingScreen = null;
    }

    private class LoadingScreen extends BlackoutScreen {
        final Array<GameObject.Definition> objectDefs;
        final Hero.Definition characterDef;
        final String mapPath;

        LoadingScreen(BlackoutGame game, GameRoom room) {
            super(game);

            // getting information from room
            objectDefs = room.getObjectDefs();
            characterDef = room.getCharacter();
            mapPath = room.getMap();
        }

        @Override
        public void show() {
            super.show();

            // start loading
            ui.load(assets);
            for (GameObject.Definition def : objectDefs) {
                assets.load(def.modelPath, Model.class);
            }
            assets.load(mapPath, Model.class);
        }

        @Override
        public void render(float delta) {
            super.render(delta);

            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            doLoading();

            float progress = assets.getProgress();
            // TODO: show progress

            // TODO: beautiful loading screen
            /*if(assets.isLoaded(LOADING_SCREEN)) {
            }*/
        }

        @Override
        public void resize(int width, int height) {
            super.resize(width, height);
        }

        @Override
        public void dispose() {
            super.dispose();
        }

        private void doLoading() {
            if (assets.update()) {
                doneLoading();
            }
        }

        private void doneLoading() {
            Gdx.app.log("blackout:GameScreen", "done loading");

            for (GameObject.Definition def : objectDefs) {
                GameObject obj = def.makeInstance(assets.get(def.modelPath, Model.class), physics);
                gameObjects.add(obj);
                if (def == characterDef) {
                    // FIXME: assert that obj instanceof Hero
                    character = (Hero)obj;
                }
            }
            // FIXME: assert that character != null

            map = new ModelInstance(assets.get(mapPath, Model.class));
            ui.doneLoading(assets, character);

            GameScreen.this.doneLoading();

            ScreenManager.getInstance().disposeScreen();
        }
    }
}
