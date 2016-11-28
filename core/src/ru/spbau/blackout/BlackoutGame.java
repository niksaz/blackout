package ru.spbau.blackout;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.physics.box2d.Box2D;

import ru.spbau.blackout.ingameui.settings.AbilityIconSettings;
import ru.spbau.blackout.ingameui.settings.IngameUISettings;
import ru.spbau.blackout.network.IdleServer;
import ru.spbau.blackout.play.services.PlayServices;
import ru.spbau.blackout.play.services.PlayServicesInCore;
import ru.spbau.blackout.gamesession.TestingSessionSettings;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.screens.LoadScreen;
import ru.spbau.blackout.settings.GameSettings;
import ru.spbau.blackout.utils.BlackoutAssets;
import ru.spbau.blackout.utils.ScreenManager;


public class BlackoutGame extends Game {
    private static final class SingletonHolder {
        public static final BlackoutGame INSTANCE = new BlackoutGame(new ScreenManager());
    }

    public static final String HOST_NAME = "192.168.1.34";
    public static final int PORT_NUMBER = 48800;

    public static final int VIRTUAL_WORLD_WIDTH = 1280;
    public static final int VIRTUAL_WORLD_HEIGHT = 768;

    // fields marked as /*final*/ must be assigned only once, but can't be assigned in constructor

    private final ScreenManager screenManager;
    private /*final*/ PlayServicesInCore playServicesInCore;

    private /*final*/ ModelBatch modelBatch;
    private /*final*/ SpriteBatch spriteBatch;
    private /*final*/ BlackoutAssets assets;

    private BlackoutGame(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }


    public static BlackoutGame get() { return SingletonHolder.INSTANCE; }
    public ModelBatch modelBatch() { return this.modelBatch; }
    public SpriteBatch spriteBatch() { return this.spriteBatch; }
    public ScreenManager screenManager() { return this.screenManager; }
    public PlayServicesInCore playServicesInCore() { return this.playServicesInCore; }
    public BlackoutAssets assets() { return this.assets; }


	// FIXME:  just for test
    public void testGameScreen() {
        AbilityIconSettings firstIconSettings = new AbilityIconSettings(0 /*num*/, 1100 /*x*/, 400 /*y*/);
        IngameUISettings uiSettings = new IngameUISettings(new AbilityIconSettings[] { firstIconSettings });
        GameSettings settings = new GameSettings(uiSettings);  // just default settings

        screenManager.setScreen(new GameScreen(TestingSessionSettings.getTest(), new IdleServer(), settings));
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

        this.assets.loadFonts();
        this.screenManager.setScreen(new LoadScreen());
	}
}
