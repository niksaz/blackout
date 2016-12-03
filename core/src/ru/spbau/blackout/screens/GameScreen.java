package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.worlds.GameWorld;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.ingameui.IngameUI;
import ru.spbau.blackout.network.AbstractServer;
import ru.spbau.blackout.gamesession.GameSessionSettings;
import ru.spbau.blackout.settings.GameSettings;

import static ru.spbau.blackout.utils.Utils.fixTop;

public class GameScreen extends BlackoutScreen {
    public static final float DEFAULT_CAMERA_X_OFFSET = 0;
    public static final float DEFAULT_CAMERA_Y_OFFSET = -1;
    public static final float DEFAULT_CAMERA_HEIGHT = 12;

    // should be here, not in loading screen because it owns all assets
    private final AssetManager assets = new AssetManager();

    // null if loading is done
    private LoadingScreen loadingScreen;

    // appearance:
    private ModelInstance map;
    private PerspectiveCamera camera;
    public Environment environment;

    private Hero character;
    private final IngameUI ui;

    private final GameWorld gameWorld;
    private final AbstractServer server;

    public GameScreen(GameSessionSettings room, GameWorld gameWorld, AbstractServer server, GameSettings settings) {
        this.server = server;
        this.gameWorld = gameWorld;
        loadingScreen = new LoadingScreen(room);
        ui = new IngameUI(this, settings.ui);

        // initialize main camera
        camera = new PerspectiveCamera();
        camera.fieldOfView = 67;
        camera.near = 1f;
        camera.far = 30000f;

        // initialize environment
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 100f));
        environment.add(new DirectionalLight().set(0.2f, 0.2f, 0.2f, 0f, 0.2f, -1f));
    }

    public AbstractServer getServer() {
        return server;
    }

    @Override
    public void show() {
        super.show();

        // if not loaded yet
        if (loadingScreen != null) {
            BlackoutGame.getInstance().getScreenManager().setScreen(loadingScreen);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        ModelBatch modelBatch = BlackoutGame.getInstance().getModelBatch();
        modelBatch.begin(camera);
        modelBatch.render(gameWorld, environment);
        modelBatch.render(map, environment);
        modelBatch.end();

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

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public Hero getCharacter() {
        return character;
    }

    /**
     * Updates game world on every frame.
     */
    private void update(final float delta) {
        gameWorld.update(delta);

        // Must go after gameWorld.update to be synced.
        Vector2 charPos = character.getPosition();
        camera.position.set(
                DEFAULT_CAMERA_X_OFFSET + charPos.x,
                DEFAULT_CAMERA_Y_OFFSET + charPos.y,
                DEFAULT_CAMERA_HEIGHT + character.getHeight());
        camera.lookAt(charPos.x, charPos.y, character.getHeight());
        camera.update();
    }

    private void doneLoading() {
        loadingScreen = null;
    }

    private class LoadingScreen extends BlackoutScreen {
        final List<GameObject.Definition> objectDefs;
        final Hero.Definition characterDef;
        final String mapPath;

        LoadingScreen(GameSessionSettings room) {
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
            for (GameObject.Definition def : objectDefs) {
                GameObject obj = def.makeInstance(assets.get(def.modelPath, Model.class), gameWorld);
                if (def == characterDef) {
                    // FIXME: assert that obj instanceof Hero
                    character = (Hero)obj;
                }
            }
            // FIXME: assert that character != null

            map = new ModelInstance(assets.get(mapPath, Model.class));
            fixTop(map);

            ui.doneLoading(assets, character);
            GameScreen.this.doneLoading();

            BlackoutGame.getInstance().getScreenManager().disposeScreen();

            // notifying server that loading is done
            synchronized (server) {
                server.notify();
            }
        }
    }
}
