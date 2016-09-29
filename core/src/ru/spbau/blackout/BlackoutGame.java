package ru.spbau.blackout;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.play.services.PlayServices;
import ru.spbau.blackout.rooms.TestingRoom;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.screens.MainMenu;

public class BlackoutGame extends Game {

	public final PlayServices playServices;

	public static final int VIRTUAL_WORLD_WIDTH = 800;
	public static final int VIRTUAL_WORLD_HEIGHT = 480;

	public ModelBatch modelBatch;
	public SpriteBatch spriteBatch;

	// FIXME:  just for test
	private void testGameScreen() {
		TestingRoom room = new TestingRoom();
		room.map =  "maps/duel/duel.g3db";

		Hero hero = new Hero("models/magician/mage.g3dj", 0, 0);
		hero.setHeight(0);
		room.hero = hero;

		setScreen(new GameScreen(this, room));
	}

	public BlackoutGame(PlayServices playServices) {
		this.playServices = playServices;
	}

	@Override
	public void create() {
		modelBatch = new ModelBatch();
		spriteBatch = new SpriteBatch();

		testGameScreen();
//		setScreen(new MainMenu(this));
	}

}
