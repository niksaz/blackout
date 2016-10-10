package ru.spbau.blackout;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.play.services.BlackoutSnapshot;
import ru.spbau.blackout.play.services.PlayServices;
import ru.spbau.blackout.play.services.PlayServicesListenerInCore;
import ru.spbau.blackout.rooms.TestingRoom;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.screens.LoadScreen;
import ru.spbau.blackout.utils.ScreenManager;

public class BlackoutGame extends Game {

	public final PlayServices playServices;
	private BlackoutSnapshot snapshot;

	public static final int VIRTUAL_WORLD_WIDTH = 1280;
	public static final int VIRTUAL_WORLD_HEIGHT = 768;

	public ModelBatch modelBatch;
	public SpriteBatch spriteBatch;

	// FIXME:  just for test
	public void testGameScreen() {
		TestingRoom room = new TestingRoom();
		room.map =  "maps/duel/duel.g3db";
		
		Hero hero = new Hero("models/magician/mage.g3db", 0, 0);
		hero.setHeight(0);
		room.hero = hero;

		ScreenManager.getInstance().setScreen(new GameScreen(this, room));
	}

	public void setSnapshot(BlackoutSnapshot snapshot) {
		this.snapshot = snapshot;
	}

	public BlackoutSnapshot getSnapshot() {
		return snapshot;
	}

	public BlackoutGame(PlayServices playServices) {
		this.playServices = playServices;
		PlayServicesListenerInCore.getInstance().initialize(this);
		playServices.setCoreListener(PlayServicesListenerInCore.getInstance());
	}

	@Override
	public void create() {
		modelBatch = new ModelBatch();
		spriteBatch = new SpriteBatch();

		ScreenManager.getInstance().initialize(this);
//		ScreenManager.getInstance().setScreen(new LoadScreen(this));
        testGameScreen();
	}

}
