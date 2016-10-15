package ru.spbau.blackout;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import ru.spbau.blackout.entities.Decoration;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.play.services.PlayServices;
import ru.spbau.blackout.play.services.PlayServicesInCore;
import ru.spbau.blackout.rooms.TestingRoom;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.screens.LoadScreen;
import ru.spbau.blackout.utils.AssetLoader;
import ru.spbau.blackout.utils.ScreenManager;

public class BlackoutGame extends Game {

    public static final String hostName = "10.181.216.201";
    public static final int portNumber = 54321;

	public static final int VIRTUAL_WORLD_WIDTH = 1280;
	public static final int VIRTUAL_WORLD_HEIGHT = 768;

	public ModelBatch modelBatch;
	public SpriteBatch spriteBatch;

	// FIXME:  just for test
	public void testGameScreen() {
		TestingRoom room = new TestingRoom();
		room.map =  "maps/duel/duel.g3db";

		room.hero = new Hero("models/wizard/wizard.g3db", 0, 0);

        GameObject stone = new Decoration("models/stone/stone.g3db", 10, 10);
        room.objects.add(stone);

		ScreenManager.getInstance().setScreen(new GameScreen(this, room));
	}

	public BlackoutGame(PlayServices playServices) {
		PlayServicesInCore.getInstance().initialize(playServices);
		playServices.setCoreListener(PlayServicesInCore.getInstance());
	}

	@Override
	public void create() {
		modelBatch = new ModelBatch();
		spriteBatch = new SpriteBatch();

		AssetLoader.getInstance().loadFonts();
		ScreenManager.getInstance().initialize(this);
		ScreenManager.getInstance().setScreen(new LoadScreen(this));
	}

}
