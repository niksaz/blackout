package ru.spbau.blackout;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.physics.box2d.Box2D;

import ru.spbau.blackout.network.SinglePlayerServer;

import ru.spbau.blackout.ingameui.settings.AbilityIconSettings;
import ru.spbau.blackout.ingameui.settings.IngameUISettings;

import ru.spbau.blackout.play.services.PlayServices;
import ru.spbau.blackout.play.services.PlayServicesInCore;
import ru.spbau.blackout.game_session.TestingSessionSettings;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.screens.LoadScreen;
import ru.spbau.blackout.settings.GameSettings;
import ru.spbau.blackout.special_effects.SpecialEffectsSystem;
import ru.spbau.blackout.utils.BlackoutAssets;
import ru.spbau.blackout.utils.ScreenManager;
import ru.spbau.blackout.worlds.GameWorldWithPhysics;


/**
 * Singleton game class which is called by libGdx when it starts a game on a platform.
 */
public class BlackoutGame extends Game {
    private static final class SingletonHolder {
        public static final BlackoutGame INSTANCE = new BlackoutGame(new ScreenManager());
    }

    public static final String HOST_NAME = "192.168.1.34";
    public static final int PORT_NUMBER = 48800;


    /** For lazy initialization (NullPointerException otherwise) */
    private static final class ScreenSizeHolder {
        public static final float ASPECT_RATION = (float)Gdx.graphics.getWidth() / (float)Gdx.graphics.getHeight();
        public static final int VIRTUAL_WORLD_WIDTH  = 1280;
        public static final int VIRTUAL_WORLD_HEIGHT = Math.round(VIRTUAL_WORLD_WIDTH / ASPECT_RATION);
    }

    public static float getAspectRation() { return ScreenSizeHolder.ASPECT_RATION; }
    public static int getWorldWidth() { return ScreenSizeHolder.VIRTUAL_WORLD_WIDTH; }
    public static int getWorldHeight() { return ScreenSizeHolder.VIRTUAL_WORLD_HEIGHT; }


    // fields marked as /*final*/ must be assigned only once, but can't be assigned in constructor

    private final ScreenManager screenManager;
    private /*final*/ PlayServicesInCore playServicesInCore;

    private /*final*/ ModelBatch modelBatch;
    private /*final*/ SpriteBatch spriteBatch;
    private /*final*/ BlackoutAssets assets;
    private /*final*/ ParticleSystem particleSystem;
    private final SpecialEffectsSystem specialEffects = new SpecialEffectsSystem();
    private GameContext context;


    private BlackoutGame(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }

    public static BlackoutGame get() { return SingletonHolder.INSTANCE; }


    public final ModelBatch modelBatch() { return this.modelBatch; }
    public final SpriteBatch spriteBatch() { return this.spriteBatch; }
    public final ScreenManager screenManager() { return this.screenManager; }
    public final PlayServicesInCore playServicesInCore() { return this.playServicesInCore; }
    public final BlackoutAssets assets() { return this.assets; }
    public final ParticleSystem particleSystem() { return this.particleSystem; }
    public final SpecialEffectsSystem specialEffects() { return this.specialEffects; }
    public final GameContext context() { return this.context; }

    public final void setContext(GameContext context) { this.context = context; }


	// FIXME:  just for test
    public void testGameScreen() {
        AbilityIconSettings firstIconSettings = new AbilityIconSettings(0);
        IngameUISettings uiSettings = new IngameUISettings(new AbilityIconSettings[] { firstIconSettings });
        GameSettings settings = new GameSettings(uiSettings);  // just default settings

        screenManager.setScreen(new GameScreen(TestingSessionSettings.getTest(), new GameWorldWithPhysics(),
                new SinglePlayerServer(), settings));
	}

    public void initializePlayServices(PlayServices playServices) {
        this.playServicesInCore = new PlayServicesInCore(playServices);
        playServices.setCoreListener(playServicesInCore);
    }

	@Override
	public void create() {
	    Box2D.init();
        // can't be created in constructor due to libgdx limitations
        this.modelBatch = new ModelBatch();
        this.spriteBatch = new SpriteBatch();
        this.assets = new BlackoutAssets();
        this.particleSystem = new ParticleSystem();

        this.assets.loadFonts();

        this.screenManager.setScreen(new LoadScreen());
	}

    @Override
    public void dispose() {
        super.dispose();
        this.screenManager.dispose();
        this.modelBatch.dispose();
        this.spriteBatch.dispose();
    }
}
