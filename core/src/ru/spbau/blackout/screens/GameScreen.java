package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.GameWorld;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.ingameui.IngameUI;
import ru.spbau.blackout.java8features.Optional;
import ru.spbau.blackout.network.AbstractServer;
import ru.spbau.blackout.game_session.GameSessionSettings;
import ru.spbau.blackout.progressbar.HorizontalProgressBar;
import ru.spbau.blackout.settings.GameSettings;
import ru.spbau.blackout.units.Vpx;
import ru.spbau.blackout.progressbar.SimpleProgressBar;
import ru.spbau.blackout.utils.Textures;

import static ru.spbau.blackout.BlackoutGame.getWorldHeight;
import static ru.spbau.blackout.BlackoutGame.getWorldWidth;
import static ru.spbau.blackout.java8features.Functional.foreach;
import static ru.spbau.blackout.utils.Utils.fixTop;

public class GameScreen extends BlackoutScreen implements GameContext {
    public static final class CameraDefaults {
        private CameraDefaults() {}
        public static final float FIELD_OF_VIEW = 30;
        public static final float X_OFFSET = 0;
        public static final float Y_OFFSET = -10;
        public static final float HEIGHT = 40;
    }


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
        this.camera.fieldOfView = CameraDefaults.FIELD_OF_VIEW;
        this.camera.near = 1f;
        this.camera.far = 300f;

        // initialize environment
        this.environment = new Environment();
        this.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 100f));
        this.environment.add(new DirectionalLight().set(0.2f, 0.2f, 0.2f, 0f, 0.2f, -1f));

        // initialize particles
        BillboardParticleBatch particleBatch = new BillboardParticleBatch();
        particleBatch.setCamera(this.camera);
        BlackoutGame.get().particleSystem().add(particleBatch);
        ParticleEffectLoader loader = new ParticleEffectLoader(new InternalFileHandleResolver());
        this.assets.setLoader(ParticleEffect.class, loader);
    }


    // instance of GameContext
    @Override
    public Optional<AssetManager> assets() {
        return Optional.of(this.assets);
    }

    @Override
    public GameWorld gameWorld() {
        return this.gameWorld;
    }

    @Override
    public void resume() {
        super.resume();
        assets.update();
    }

    // instance of Screen
    @Override
    public void show() {
        super.show();

        // if not loaded yet
        if (this.loadingScreen != null) {
            BlackoutGame.get().screenManager().setScreen(this.loadingScreen);
        }
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // model batch rendering
        {
            ModelBatch modelBatch = BlackoutGame.get().modelBatch();
            ParticleSystem particleSystem = BlackoutGame.get().particleSystem();

            modelBatch.begin(this.camera);

            modelBatch.render(this.gameWorld, this.environment);
            modelBatch.render(this.map, this.environment);

            // render particles
            particleSystem.update();
            particleSystem.begin();
            particleSystem.draw();
            particleSystem.end();
            modelBatch.render(particleSystem);

            modelBatch.end();
        }

        this.ui.update(deltaTime);
        this.ui.draw();

        this.gameWorld.update(deltaTime);
        this.updateCamera();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void dispose() {
        super.dispose();
        this.assets.dispose();
        this.gameWorld.dispose();
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


    private void updateCamera() {
        // Must go after gameWorld.update to be synced.
        Vector2 charPos = character.getPosition();
        camera.position.set(
                CameraDefaults.X_OFFSET + charPos.x,
                CameraDefaults.Y_OFFSET + charPos.y,
                CameraDefaults.HEIGHT + character.getHeight());
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
        public static final String BACKGROUND_IMAGE = "images/loading_screen.png";


        private final List<GameObject.Definition> objectDefs;
        private final Character.Definition characterDef;
        private final String mapPath;
        private final Stage stage;
        private final SimpleProgressBar progressBar =
                new HorizontalProgressBar(ProgressBarConst.PATH_EMPTY, ProgressBarConst.PATH_FULL);
        private boolean loadingScreenLoaded = false;


        public LoadingScreen(GameSessionSettings room) {
            // getting information from room
            this.objectDefs = room.getObjectDefs();
            this.characterDef = room.getCharacter();
            this.mapPath = room.getMap();

            Camera camera = new OrthographicCamera();
            Viewport viewport = new StretchViewport(getWorldWidth(), getWorldHeight(), camera);
            this.stage = new Stage(viewport, BlackoutGame.get().spriteBatch());

            float startX = (getWorldWidth() - ProgressBarConst.WIDTH) / 2;
            this.progressBar.setPosition(startX, ProgressBarConst.START_Y);
            this.progressBar.setSize(ProgressBarConst.WIDTH, ProgressBarConst.HEIGHT);
        }


        @Override
        public void show() {
            super.show();
            // first of all, it loads its own resources.
            this.progressBar.load(assets);
            Textures.loadFast(BACKGROUND_IMAGE, assets);
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
                    // show loading screen
                    float progress = assets.getProgress();
                    this.progressBar.setValue(progress);
                    this.stage.act();
                    this.stage.draw();
                }
            } else if (loaded) {
                // initialize loading screen and start loading real resources
                this.initializeLoadingScreen();
                this.loadRealResources();
                loadingScreenLoaded = true;
            }
        }

        @Override
        public void resize(int width, int height) {
            super.resize(width, height);
            this.stage.getViewport().update(Vpx.fromRpx(width), Vpx.fromRpx(height));
        }

        @Override
        public void dispose() {
            super.dispose();
            assets.unload(BACKGROUND_IMAGE);
            assets.unload(ProgressBarConst.PATH_EMPTY);
            assets.unload(ProgressBarConst.PATH_FULL);
        }

        private void initializeLoadingScreen() {
            // background image
            Texture backgroundTexture = assets.get(BACKGROUND_IMAGE, Texture.class);
            Image background = new Image(backgroundTexture);
            background.setPosition(0, 0);
            background.setSize(getWorldWidth(), getWorldHeight());
            this.stage.addActor(background);

            // progress bar
            this.progressBar.doneLoading(assets);
            this.progressBar.toFront();
            this.stage.addActor(this.progressBar);

            // label
            Label.LabelStyle style = new Label.LabelStyle(
                    BlackoutGame.get().assets().getFont(),
                    LoadingLabelConst.COLOR
            );
            Label label = new Label(
                "Test: The pen name, Max Frei, was invented by Martynchik and Steopin for their works on" +
                    "comic fantasy series Labyrinths of Echo (\"ЛабиринтыEхо\"). The plot follows the eponymous" +
                    "narrator, sir Max, as he leaves our \"real\" world...",
                style
            );
            label.setPosition(LoadingLabelConst.MIN_X, LoadingLabelConst.MIN_Y);
            label.setSize(LoadingLabelConst.WIDTH, LoadingLabelConst.MAX_Y - LoadingLabelConst.MIN_Y);
            label.setAlignment(Align.center);
            label.setWrap(true);
            this.stage.addActor(label);
        }

        private void loadRealResources() {
            ui.load(assets);
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

            ui.doneLoading(assets, character);
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
        private static final float WIDTH = Math.min(Vpx.fromCm(8f), getWorldWidth() / 2);
        private static final float HEIGHT = Math.min(Vpx.fromCm(1.3f), getWorldHeight() / 8);
        private static final float START_Y = getWorldHeight() / 5;
    }


    /** constant holder for label in loading screen */
    private static final class LoadingLabelConst {
        private LoadingLabelConst() {}
        private static final Color COLOR = new Color(60f / 255f, 10f / 255f, 0, 1);

        private static final float MAX_Y = getWorldHeight() * 4 / 5;
        private static final float MIN_Y = getWorldHeight() * 2 / 5;
        private static final float WIDTH = getWorldWidth() / 2;
        private static final float MIN_X = (getWorldWidth() - LoadingLabelConst.WIDTH) / 2;
    }
}
