package ru.spbau.blackout;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.physics.box2d.Box2D;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.fireball.FireballAbility;
import ru.spbau.blackout.entities.Decoration;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.ingameui.settings.AbilityIconSettings;
import ru.spbau.blackout.ingameui.settings.IngameUISettings;
import ru.spbau.blackout.network.IdleServer;
import ru.spbau.blackout.play.services.PlayServices;
import ru.spbau.blackout.play.services.PlayServicesInCore;
import ru.spbau.blackout.gamesession.TestingSessionSettings;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.screens.LoadScreen;
import ru.spbau.blackout.settings.GameSettings;
import ru.spbau.blackout.shapescreators.CircleCreator;
import ru.spbau.blackout.units.Rpx;
import ru.spbau.blackout.units.Vpx;
import ru.spbau.blackout.utils.AssetLoader;
import ru.spbau.blackout.utils.ScreenManager;


public class BlackoutGame extends Game {

    private static final BlackoutGame INSTANCE = new BlackoutGame(new ScreenManager());

    public static final String HOST_NAME = "192.168.1.34";
    public static final int PORT_NUMBER = 48800;

    public static final int VIRTUAL_WORLD_WIDTH = 1280;
    public static final int VIRTUAL_WORLD_HEIGHT = 768;

    private ScreenManager screenManager;
    private PlayServicesInCore playServicesInCore;

    // not supposed to be changed, but can't be initialized in constructor
    // due to libgdx limitations
    private ModelBatch modelBatch;
    private SpriteBatch spriteBatch;


    public ModelBatch getModelBatch() {
        return modelBatch;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public static BlackoutGame getInstance() {
        return INSTANCE;
    }

    private BlackoutGame(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }

    public ScreenManager getScreenManager() {
        return screenManager;
    }

    public PlayServicesInCore getPlayServicesInCore() {
        return playServicesInCore;
    }

	// FIXME:  just for test
    public void testGameScreen() {
        AbilityIconSettings firstIconSettings = new AbilityIconSettings(0 /*num*/, 500 /*x*/, 100 /*y*/);
        IngameUISettings uiSettings = new IngameUISettings(new AbilityIconSettings[] { firstIconSettings });
        GameSettings settings = new GameSettings(uiSettings);  // just default settings

        screenManager.setScreen(new GameScreen(TestingSessionSettings.getTest(), new IdleServer(), settings));
	}

    public void initializePlayServices(PlayServices playServices) {
        playServicesInCore = new PlayServicesInCore(playServices);
        playServices.setCoreListener(playServicesInCore);
    }

	@Override
	public void create() {
	    Box2D.init();
        // can't be created in constructor due to libgdx limitations
        modelBatch = new ModelBatch();
        spriteBatch = new SpriteBatch();

		AssetLoader.getInstance().loadFonts();
        screenManager.setScreen(new LoadScreen());
	}
}
