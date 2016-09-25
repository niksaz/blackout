package ru.spbau.blackout;

import com.badlogic.gdx.Game;

import ru.spbau.blackout.rooms.GameRoom;
import ru.spbau.blackout.rooms.TeamsRoundsRoom;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.screens.MainMenu;

public class BlackoutGame extends Game {

	@Override
	public void create () {
		GameRoom room = new TeamsRoundsRoom();
		room.setMap("maps/duel.tmx");
		setScreen(new GameScreen(this, room));
	}

}
