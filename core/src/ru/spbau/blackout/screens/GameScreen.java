package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.List;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.GameWorld;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.ingameui.IngameUI;
import ru.spbau.blackout.java8features.Optional;
import ru.spbau.blackout.network.AbstractServer;
import ru.spbau.blackout.gamesession.GameSessionSettings;
import ru.spbau.blackout.settings.GameSettings;
import ru.spbau.blackout.units.Rpx;
import ru.spbau.blackout.utils.SimpleProgressBar;

import static ru.spbau.blackout.BlackoutGame.VIRTUAL_WORLD_HEIGHT;
import static ru.spbau.blackout.java8features.Functional.foreach;
import static ru.spbau.blackout.utils.Utils.fixTop;

public class GameScreen extends BlackoutScreen implements GameContext {
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

    private Character character;
    private final IngameUI ui;

    private final GameWorld gameWorld = new GameWorld();
    private final AbstractServer server;
    private volatile boolean doneLoading;


    public GameScreen(GameSessionSettings sessionSettings, AbstractServer server, GameSettings settings) {
        this.server = server;
        this.loadingScreen = new LoadingScreen(sessionSettings);
        this.ui = new IngameUI(this.getServer(), settings.ui);

        // initialize main camera
        this.camera = new PerspectiveCamera();
        this.camera.fieldOfView = 67;
        this.camera.near = 1f;
        this.camera.far = 30000f;

        // initialize environment
        this.environment = new Environment();
        this.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 100f));
        this.environment.add(new DirectionalLight().set(0.2f, 0.2f, 0.2f, 0f, 0.2f, -1f));
    }


    // instance of GameContext
    @Override
    public Optional<AssetManager> assets() {
        return Optional.of(assets);
    }

    @Override
    public GameWorld gameWorld() {
        return gameWorld;
    }


    // instance of Screen
    @Override
    public void show() {
        super.show();

        // if not loaded yet
        if (loadingScreen != null) {
            BlackoutGame.get().screenManager().setScreen(loadingScreen);
        }
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        ModelBatch modelBatch = BlackoutGame.get().modelBatch();
        modelBatch.begin(camera);
        modelBatch.render(gameWorld, environment);
        modelBatch.render(map, environment);
        modelBatch.end();

        ui.update(deltaTime);  // FIXME: should be in common update method
        ui.draw();

        update(deltaTime);  // FIXME: why it is in the end of render. It doesn't look logical.
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


    public AbstractServer getServer() {
        return server;
    }

    public Character getCharacter() {
        return character;
    }

    public boolean isDoneLoading() {
        return doneLoading;
    }


    /**
     * Updates game world on every frame.
     */
    private void update(final float deltaTime) {  // FIXME: m.b. this method is redundant
        synchronized (gameWorld) {
            gameWorld.update(deltaTime);
        }

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


    /**
     * Screen with progress bar which is showed during loading assets.
     */
    private class LoadingScreen extends BlackoutScreen {
        private final List<GameObject.Definition> objectDefs;
        private final Character.Definition characterDef;
        private final String mapPath;
        private final Stage stage;
        private final SimpleProgressBar progressBar =
                new SimpleProgressBar(ProgressBarConst.PATH_EMPTY, ProgressBarConst.PATH_FULL);
        private boolean loadingScreenLoaded = false;


        public LoadingScreen(GameSessionSettings room) {
            // getting information from room
            this.objectDefs = room.getObjectDefs();
            this.characterDef = room.getCharacter();
            this.mapPath = room.getMap();

            Camera camera = new OrthographicCamera();
            this.stage = new Stage(new ScreenViewport(camera), BlackoutGame.get().spriteBatch());

            float startX = (Gdx.graphics.getWidth() - ProgressBarConst.WIDTH) / 2;
            this.progressBar.setPosition(startX, ProgressBarConst.START_Y);
            this.progressBar.setSize(ProgressBarConst.WIDTH, ProgressBarConst.HEIGHT);
        }


        @Override
        public void show() {
            super.show();
            // first of all, it loads its own resources.
            this.progressBar.load(assets);
        }

        @Override
        public void render(float delta) {
            super.render(delta);

            // fill screen by gray color
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            // do loading
            boolean loaded = assets.update();

            if (loadingScreenLoaded) {
                if (loaded) {
                    // end loading
                    this.doneLoading();
                } else {
                    // show progress bar
                    float progress = assets.getProgress();
                    System.out.println(progress);
                    this.progressBar.setNormalizedValue(progress);
                    this.stage.act();
                    this.stage.draw();
                }
            } else if (loaded) {
                // initialize progress bar, start loading real resources
                progressBar.doneLoading(assets);
                this.stage.addActor(progressBar);
                this.loadRealResources();
                loadingScreenLoaded = true;
            }
        }

        @Override
        public void resize(int width, int height) {
            super.resize(width, height);
        }

        @Override
        public void dispose() {
            super.dispose();
            // TODO: dispose progress bar's resources
        }


        private void loadRealResources() {
            ui.load(GameScreen.this);
            foreach(this.objectDefs, def -> def.load(GameScreen.this));
            assets.load(this.mapPath, Model.class);
        }

        private void doneLoading() {
            for (GameObject.Definition def : objectDefs) {
                def.doneLoading(GameScreen.this);
                GameObject obj = def.makeInstance();
                if (def == characterDef) {
                    character = (Character) obj;
                }
            }
            if (character == null) {
                throw new AssertionError("Player without character");
            }

            map = new ModelInstance(assets.get(mapPath, Model.class));
            fixTop(map);

            ui.doneLoading(GameScreen.this, character);
            GameScreen.this.doneLoading();

            BlackoutGame.get().screenManager().disposeScreen();

            doneLoading = true;
        }
    }


    /** constant holder for progress bar in loading screen */
    private static final class ProgressBarConst {
        private ProgressBarConst() {}
        private static final String PATH_EMPTY = "images/progress_bar/empty.png";
        private static final String PATH_FULL = "images/progress_bar/full.png";
        // current progress bar isn't scalable.
        private static final float WIDTH = Rpx.X.fromCm(6f);
        private static final float HEIGHT = Rpx.Y.fromCm(1f);
        private static final float START_Y = Rpx.Y.fromVpx(VIRTUAL_WORLD_HEIGHT / 10);
    }
}
