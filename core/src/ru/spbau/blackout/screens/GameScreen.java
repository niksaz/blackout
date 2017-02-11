package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.jetbrains.annotations.Nullable;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.ingameui.IngameUI;
import ru.spbau.blackout.ingameui.ObserverUI;
import ru.spbau.blackout.sessionsettings.SessionSettings;
import ru.spbau.blackout.network.UIServer;
import ru.spbau.blackout.worlds.GameWorld;
import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.ingameui.PlayerUI;
import ru.spbau.blackout.progressbar.HorizontalProgressBar;
import ru.spbau.blackout.settings.GameSettings;
import ru.spbau.blackout.units.Vpx;
import ru.spbau.blackout.progressbar.SimpleProgressBar;
import ru.spbau.blackout.utils.Textures;

import static ru.spbau.blackout.BlackoutGame.getWorldHeight;
import static ru.spbau.blackout.BlackoutGame.getWorldWidth;
import static ru.spbau.blackout.java8features.Functional.foreach;
import static ru.spbau.blackout.settings.GameSettings.MUSIC_MAX_VOLUME;


public class GameScreen extends BlackoutScreen implements GameContext {

    private static final String BATTLE_MUSIC_DIR_PATH = "music/battle";

    public static final class CameraDefaults {
        private CameraDefaults() {}

        public static final float FIELD_OF_VIEW = 30;
        public static final float X_OFFSET = 0;
        public static final float Y_OFFSET = -10;
        public static final float HEIGHT = 40;
    }


    // should be here, not in loading getScreen because it owns all assets
    private final AssetManager assets = new AssetManager();

    // null if loading is done
    private LoadingScreen loadingScreen;

    // appearance:
    private final PerspectiveCamera camera;
    public final Environment environment;

    @Nullable private Character mainCharacter;
    private IngameUI ui;

    private final GameWorld gameWorld;
    private final UIServer uiServer;

    private final Array<Music> music = new Array<>();
    private Music currentTrack;
    private final GameSettings settings;
    private final ParticleSystem particleSystem = new ParticleSystem();


    public GameScreen(SessionSettings sessionSettings,
                      GameWorld gameWorld,
                      UIServer uiServer,
                      GameSettings settings) {
        this.settings = settings;
        this.gameWorld = gameWorld;
        this.uiServer = uiServer;

        loadingScreen = new LoadingScreen(sessionSettings);
        ui = new PlayerUI(getUiServer());

        // initializeGameWorld main camera
        camera = new PerspectiveCamera();
        camera.fieldOfView = CameraDefaults.FIELD_OF_VIEW;
        camera.near = 1f;
        camera.far = 300f;

        // initializeGameWorld environment
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 100f));
        environment.add(new DirectionalLight().set(0.2f, 0.2f, 0.2f, 0f, 0.2f, -1f));

        // initializeGameWorld particles
        BillboardParticleBatch particleBatch = new BillboardParticleBatch();
        particleBatch.setCamera(camera);
        particleSystem.add(particleBatch);
        ParticleEffectLoader loader = new ParticleEffectLoader(new InternalFileHandleResolver());
        assets.setLoader(ParticleEffect.class, loader);

        // initializeGameWorld music
        FileHandle battleMusicDir = Gdx.files.internal(BATTLE_MUSIC_DIR_PATH);
        for (FileHandle file : battleMusicDir.list()) {
            Music track = Gdx.audio.newMusic(file);
            track.setVolume(settings.musicVolume * MUSIC_MAX_VOLUME);
            track.setLooping(false);
            music.add(track);
        }
    }


    /**
     * Switches current music track to a random one which isn't equal to the current.
     */
    public void switchTrack() {
        if (currentTrack != null) {
            currentTrack.stop();
        }

        Music oldTrack = currentTrack;
        while (currentTrack == oldTrack) {
            currentTrack = music.random();
        }
        currentTrack.play();
    }

    public IngameUI getUi() { return ui; }

    public PerspectiveCamera getCamera() { return camera; }

    @Override
    public boolean hasUI() {
        return true;
    }

    // instance of GameContext
    @Override
    public AssetManager getAssets() {
        return assets;
    }

    @Override
    public GameWorld gameWorld() {
        return gameWorld;
    }

    @Override
    public GameScreen getScreen() {
        return this;
    }

    @Override
    public GameSettings getSettings() {
        return settings;
    }

    @Override
    public ParticleSystem getParticleSystem() {
        return particleSystem;
    }

    @Override
    @Nullable
    public Character getMainCharacter() {
        return mainCharacter;
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

        // enable music
        switchTrack();

        // if not loaded yet
        if (loadingScreen != null) {
            BlackoutGame.get().screenManager().setScreen(loadingScreen);
        }
    }

    public void becomeObserver() {
        mainCharacter = null;
        IngameUI previousUi = ui;
        ui = new ObserverUI(previousUi, uiServer, camera);
        previousUi.dispose();
        ui.load(this);
        assets.finishLoading();
        ui.doneLoading(this);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (mainCharacter != null && mainCharacter.isDead()) {
            becomeObserver();
        }

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(0, 0, 0, 1);

        // music
        if (!currentTrack.isPlaying()) {
            switchTrack();
        }

        // world
        gameWorld.updatePhysics(delta);

        if (mainCharacter != null) {
            moveCameraToPlayer();
        }
        camera.update();

        gameWorld.updateGraphic(delta);

        // special effects
        BlackoutGame.get().specialEffects().update(delta);

        ui.update(delta);

        // modelInstance batch rendering
        {
            ModelBatch modelBatch = BlackoutGame.get().modelBatch();

            modelBatch.begin(camera);

            modelBatch.render(gameWorld.getGameObjects(), environment);

            // render particles
            particleSystem.update();
            particleSystem.begin();
            particleSystem.draw();
            particleSystem.end();
            modelBatch.render(particleSystem);

            modelBatch.end();
        }

        // ui
        ui.draw();
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
        gameWorld.dispose();
        currentTrack.stop();
        ui.dispose();
        foreach(music, Music::dispose);
        assets.dispose();
    }

    public UIServer getUiServer() {
        return uiServer;
    }

    private void moveCameraToPlayer() {
        assert mainCharacter != null;
        // Must go after gameWorld.updatePhysics to be synced.
        Vector2 charPos = mainCharacter.getPosition();
        camera.position.set(
                CameraDefaults.X_OFFSET + charPos.x,
                CameraDefaults.Y_OFFSET + charPos.y,
                CameraDefaults.HEIGHT + mainCharacter.getHeight());
        camera.lookAt(charPos.x, charPos.y, mainCharacter.getHeight());
    }

    private void doneLoading() {
        loadingScreen = null;
    }


    /**
     * Screen with progress bar which is showed during loading assets.
     */
    private class LoadingScreen extends BlackoutScreen {
        public static final String BACKGROUND_IMAGE = "images/loading_screen.png";


        private final Stage stage;
        private final SimpleProgressBar progressBar =
                new HorizontalProgressBar(LoadingProgressBar.PATH_EMPTY, LoadingProgressBar.PATH_FULL);
        private boolean loadingScreenLoaded = false;
        private final SessionSettings sessionSettings;

        public LoadingScreen(SessionSettings sessionSettings) {
            this.sessionSettings = sessionSettings;

            Camera camera = new OrthographicCamera();
            Viewport viewport = new StretchViewport(getWorldWidth(), getWorldHeight(), camera);
            stage = new Stage(viewport, BlackoutGame.get().spriteBatch());

            float startX = (getWorldWidth() - LoadingProgressBar.WIDTH) / 2;
            progressBar.setPosition(startX, LoadingProgressBar.START_Y);
            progressBar.setSize(LoadingProgressBar.WIDTH, LoadingProgressBar.HEIGHT);
        }


        @Override
        public void show() {
            super.show();
            // first of all, it loads its own resources.
            progressBar.load(assets);
            Textures.loadFast(BACKGROUND_IMAGE, assets);
        }

        @Override
        public void render(float delta) {
            super.render(delta);

            // fill getScreen by gray color
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            // do loading
            boolean loaded = assets.update();

            if (loadingScreenLoaded) {
                if (loaded) {
                    // end loading
                    doneLoading();
                } else {
                    // show loading getScreen
                    float progress = assets.getProgress();
                    progressBar.setValue(progress);
                    stage.act();
                    stage.draw();
                }
            } else if (loaded) {
                // initializeGameWorld loading getScreen and start loading real resources
                initializeLoadingScreen();
                loadRealResources();
                loadingScreenLoaded = true;
            }
        }

        @Override
        public void dispose() {
            super.dispose();
            assets.unload(BACKGROUND_IMAGE);
            assets.unload(LoadingProgressBar.PATH_EMPTY);
            assets.unload(LoadingProgressBar.PATH_FULL);
        }

        private void initializeLoadingScreen() {
            // background image
            Texture backgroundTexture = assets.get(BACKGROUND_IMAGE, Texture.class);
            Image background = new Image(backgroundTexture);
            background.setPosition(0, 0);
            background.setSize(getWorldWidth(), getWorldHeight());
            stage.addActor(background);

            // progress bar
            progressBar.doneLoading(assets);
            progressBar.toFront();
            stage.addActor(progressBar);

            // label
            Label.LabelStyle style = new Label.LabelStyle(
                    BlackoutGame.get().assets().getFont(),
                    LoadingLabel.COLOR
            );
            Label label = new Label(
                    "Test: The pen name, Max Frei, was invented by Martynchik and Steopin for their works on" +
                            "comic fantasy series Labyrinths of Echo (\"ЛабиринтыEхо\"). The plot follows the eponymous" +
                            "narrator, sir Max, as he leaves our \"real\" world...",
                    style
            );
            label.setPosition(LoadingLabel.MIN_X, LoadingLabel.MIN_Y);
            label.setSize(LoadingLabel.WIDTH, LoadingLabel.MAX_Y - LoadingLabel.MIN_Y);
            label.setAlignment(Align.center);
            label.setWrap(true);
            stage.addActor(label);
        }

        private void loadRealResources() {
            ui.load(GameScreen.this);
            gameWorld().load(GameScreen.this);
        }

        private void doneLoading() {
            gameWorld.doneLoading();
            sessionSettings.initializeGameWorld();

            System.out.println("Player uid " + sessionSettings.getPlayerUid());

            mainCharacter = (Character) gameWorld.getObjectById(sessionSettings.getPlayerUid());

            if (mainCharacter == null) {
                throw new AssertionError("Player without mainCharacter");
            }

            ui.doneLoading(GameScreen.this);
            GameScreen.this.doneLoading();

            BlackoutGame.get().screenManager().disposeScreen();

            // notifying uiServer that loading is done
            synchronized (uiServer) {
                uiServer.notify();
            }
        }
    }


    /**
     * constant holder for progress bar in loading getScreen
     */
    private static final class LoadingProgressBar {
        private LoadingProgressBar() {}

        public static final String PATH_EMPTY = "images/progress_bar/empty.png";
        public static final String PATH_FULL = "images/progress_bar/full.png";
        public static final float WIDTH = Math.min(Vpx.fromCm(8f), getWorldWidth() / 2);
        public static final float HEIGHT = Math.min(Vpx.fromCm(1.3f), getWorldHeight() / 8);
        public static final float START_Y = getWorldHeight() / 5;
    }


    /**
     * constant holder for label in loading getScreen
     */
    private static final class LoadingLabel {
        private LoadingLabel() {}

        public static final Color COLOR = new Color(60f / 255f, 10f / 255f, 0, 1);

        public static final float MAX_Y = getWorldHeight() * 4 / 5;
        public static final float MIN_Y = getWorldHeight() * 2 / 5;
        public static final float WIDTH = getWorldWidth() / 2;
        public static final float MIN_X = (getWorldWidth() - LoadingLabel.WIDTH) / 2;
    }
}
