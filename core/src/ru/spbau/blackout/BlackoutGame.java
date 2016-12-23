package ru.spbau.blackout;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.physics.box2d.Box2D;

import ru.spbau.blackout.androidfeatures.PlayServices;
import ru.spbau.blackout.androidfeatures.PlayServicesInCore;
import ru.spbau.blackout.database.PlayerEntity;
import ru.spbau.blackout.ingameui.settings.AbilityIconSettings;
import ru.spbau.blackout.ingameui.settings.IngameUISettings;
import ru.spbau.blackout.network.SinglePlayerServer;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.screens.LoadScreen;
import ru.spbau.blackout.sessionsettings.SessionSettings;
import ru.spbau.blackout.settings.GameSettings;
import ru.spbau.blackout.specialeffects.SpecialEffectsSystem;
import ru.spbau.blackout.utils.BlackoutAssets;
import ru.spbau.blackout.utils.ScreenManager;
import ru.spbau.blackout.worlds.GameWorld;
import ru.spbau.blackout.worlds.ServerGameWorld;


/**
 * Singleton game class which is called by libGdx when it starts a game on a platform.
 */
public class BlackoutGame extends Game {

    private static final class SingletonHolder {
        public static final BlackoutGame INSTANCE = new BlackoutGame(new ScreenManager());
    }


    public static float getAspectRatio() {
        return (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();
    }

    public static int getWorldWidth() {
        return 1280;
    }

    public static int getWorldHeight() {
        return Math.round(getWorldWidth() / getAspectRatio());
    }


    // fields marked as /*final*/ must be assigned only once, but can't be assigned in constructor

    private volatile PlayerEntity playerEntity;
    private final ScreenManager screenManager;
    private /*final*/ PlayServicesInCore playServicesInCore;

    private /*final*/ ModelBatch modelBatch;
    private /*final*/ SpriteBatch spriteBatch;
    private /*final*/ BlackoutAssets assets;
    private /*final*/ ParticleSystem particleSystem;
    private final SpecialEffectsSystem specialEffects = new SpecialEffectsSystem();

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


	// FIXME:  just for test
    public void startTestSinglePlayerGame() {
        AbilityIconSettings firstIconSettings = new AbilityIconSettings(0);
        IngameUISettings uiSettings = new IngameUISettings(new AbilityIconSettings[] { firstIconSettings });
        GameSettings settings = new GameSettings(uiSettings);  // just default settings
        SessionSettings sessionSettings = SessionSettings.getTest();

        GameWorld gameWorld = new ServerGameWorld(sessionSettings.getDefinitions());

        screenManager.setScreen(new GameScreen(sessionSettings, gameWorld, new SinglePlayerServer(), settings));
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

        this.assets.load();

        this.screenManager.setScreen(new LoadScreen());

        // FIXME: remove
        /*SessionSettings sessionSettings = SessionSettings.getTest();
        List<GameObject.Definition> l = sessionSettings.getDefinitions();
        List<GameObject.Definition> allDefs = ReflectUtils.findAllImpls(l, GameObject.Definition.class);
        System.out.println(allDefs.size());
        for (GameObject.Definition def : allDefs) {
            Gdx.app.log("QWERTY", def.getClass().getName());
        }*/
    }

    @Override
    public void dispose() {
        super.dispose();
        this.screenManager.dispose();
        this.modelBatch.dispose();
        this.spriteBatch.dispose();
    }

    public PlayerEntity getPlayerEntity() {
        return playerEntity;
    }

    public void setPlayerEntity(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }
}
