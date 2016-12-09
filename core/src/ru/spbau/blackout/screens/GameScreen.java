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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.worlds.GameWorld;
import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.graphic_effects.GraphicEffect;
import ru.spbau.blackout.graphic_effects.HealthBarEffect;
import ru.spbau.blackout.ingameui.IngameUI;
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
    private static final String BATTLE_MUSIC_DIR_PATH = "music/battle";
    private static final float BATTLE_MUSIC_MAX_VOLUME = 0.4f;

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

    private final GameWorld gameWorld;
    private final AbstractServer server;

    private final Array<Music> music = new Array<>();
    private Music currentTrack;
    private final GameSettings settings;


    public GameScreen(GameSessionSettings sessionSettings, GameWorld gameWorld, AbstractServer server,
                GameSettings settings) {
        this.settings = settings;
        this.gameWorld = gameWorld;
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

        // initialize music
        FileHandle battleMusicDir = Gdx.files.internal(BATTLE_MUSIC_DIR_PATH);
        for (FileHandle file : battleMusicDir.list()) {
            Music track = Gdx.audio.newMusic(file);
            track.setVolume(settings.battleMusicVolume * BATTLE_MUSIC_MAX_VOLUME);
            track.setLooping(false);
            this.music.add(track);
        }
    }


    /**
     * Switches current music track to a random one which isn't equal to the current.
     */
    public void switchTrack() {
        if (this.currentTrack != null) {
            this.currentTrack.stop();
        }

        Music oldTrack = this.currentTrack;
        while (this.currentTrack == oldTrack) {
            this.currentTrack = this.music.random();
        }
        this.currentTrack.play();
    }

    @Override
    public boolean hasIO() {
        return true;
    }

    // instance of GameContext
    @Override
    public AssetManager getAssets() {
        return this.assets;
    }

    @Override
    public GameWorld gameWorld() {
        return this.gameWorld;
    }

    @Override
    public GameSettings getSettings() {
        return this.settings;
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

        BlackoutGame.get().setContext(this);

        // enable music
        this.switchTrack();

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

        BlackoutGame game = BlackoutGame.get();

        // model batch rendering
        {
            ModelBatch modelBatch = game.modelBatch();
            ParticleSystem particleSystem = game.particleSystem();

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

        // special effects
        game.specialEffects().update(deltaTime);

        // music
        if (!this.currentTrack.isPlaying()) {
            this.switchTrack();
        }

        // world
        this.gameWorld.update(deltaTime);
        this.updateCamera();

        // ui
        this.ui.update(deltaTime);
        this.ui.draw();
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
        foreach(this.music, Music::dispose);
    }


    public AbstractServer getServer() {
        return server;
    }

    public Character getCharacter() {
        return character;
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
                new HorizontalProgressBar(LoadingProgressBar.PATH_EMPTY, LoadingProgressBar.PATH_FULL);
        private boolean loadingScreenLoaded = false;
        private final SimpleProgressBar commonHealthBar =
                new HorizontalProgressBar(SmallHealthBar.PATH_EMPTY, SmallHealthBar.PATH_FULL);


        public LoadingScreen(GameSessionSettings room) {
            // getting information from room
            this.objectDefs = room.getObjectDefs();
            this.characterDef = room.getCharacter();
            this.mapPath = room.getMap();

            Camera camera = new OrthographicCamera();
            Viewport viewport = new StretchViewport(getWorldWidth(), getWorldHeight(), camera);
            this.stage = new Stage(viewport, BlackoutGame.get().spriteBatch());

            float startX = (getWorldWidth() - LoadingProgressBar.WIDTH) / 2;
            this.progressBar.setPosition(startX, LoadingProgressBar.START_Y);
            this.progressBar.setSize(LoadingProgressBar.WIDTH, LoadingProgressBar.HEIGHT);

            this.commonHealthBar.setSize(SmallHealthBar.WIDTH, SmallHealthBar.HEIGHT);
            this.commonHealthBar.toBack();
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
            this.stage.addActor(background);

            // progress bar
            this.progressBar.doneLoading(assets);
            this.progressBar.toFront();
            this.stage.addActor(this.progressBar);

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
            this.stage.addActor(label);
        }

        private void loadRealResources() {
            ui.load(assets);
            foreach(this.objectDefs, def -> def.load());
            assets.load(this.mapPath, Model.class);
            this.commonHealthBar.load(assets);
        }

        private void doneLoading() {
            this.commonHealthBar.doneLoading(assets);

            for (GameObject.Definition def : objectDefs) {
                def.doneLoading();
                GameObject obj = def.makeInstance();

                if (def == characterDef) {
                    character = (Character) obj;
                } else if (obj instanceof Character) {
                    SimpleProgressBar healthBar = commonHealthBar.copy();
                    ui.stage.addActor(healthBar);
                    GraphicEffect healthBarEffect = new HealthBarEffect((GameUnit) obj, healthBar, camera);
                    obj.graphicEffects.add(healthBarEffect);
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

            // notifying server that loading is done
            synchronized (server) {
                server.notify();
            }
        }
    }


    /**
     * constant holder for progress bar in loading screen
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
     * constant holder for label in loading screen
     */
    private static final class LoadingLabel {
        private LoadingLabel() {}

        public static final Color COLOR = new Color(60f / 255f, 10f / 255f, 0, 1);

        public static final float MAX_Y = getWorldHeight() * 4 / 5;
        public static final float MIN_Y = getWorldHeight() * 2 / 5;
        public static final float WIDTH = getWorldWidth() / 2;
        public static final float MIN_X = (getWorldWidth() - LoadingLabel.WIDTH) / 2;
    }

    private static final class SmallHealthBar {
        SmallHealthBar() {}

        public static final String PATH_FULL = "images/health_bar/full.png";
        public static final String PATH_EMPTY = "images/health_bar/empty.png";

        public static final float WIDTH = getWorldWidth() * 0.06f;
        public static final float HEIGHT = getWorldHeight() * 0.013f;
    }
}