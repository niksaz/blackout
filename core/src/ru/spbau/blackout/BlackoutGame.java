package ru.spbau.blackout;

import com.badlogic.gdx.Game;

import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.rooms.TestingRoom;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.screens.MainMenu;

public class BlackoutGame extends Game {
	// FIXME:  just for test
	private void testGameScreen() {
		TestingRoom room = new TestingRoom();
		room.map =  "maps/duel.tmx";

		Hero hero = new Hero("models/magician/mage.g3dj", 0, 0);
		hero.setHeight(0);
		room.units.add(hero);

		setScreen(new GameScreen(this, room));
	}

	@Override
	public void create () {
		setScreen(new MainMenu(this));
	}

}
