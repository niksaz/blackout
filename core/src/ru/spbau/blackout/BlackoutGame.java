package ru.spbau.blackout;

import com.badlogic.gdx.Game;
import com.sun.corba.se.impl.orb.ParserTable;

import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.rooms.GameRoom;
import ru.spbau.blackout.rooms.TestingRoom;
import ru.spbau.blackout.screens.GameScreen;

public class BlackoutGame extends Game {
	@Override
	public void create () {
		// FIXME:  just for test
		TestingRoom room = new TestingRoom();
		room.map =  "maps/duel.tmx";

		Hero hero = new Hero("models/magician/mage.g3dj", 0, 0);
		hero.setHeight(0);
		room.units.add(hero);

		setScreen(new GameScreen(this, room));
	}
}
