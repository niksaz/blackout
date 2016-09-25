package ru.spbau.blackout;

import com.badlogic.gdx.Game;

import ru.spbau.blackout.screens.MainMenu;

public class BlackoutGame extends Game {

	@Override
	public void create () {
		setScreen(new MainMenu(this));
	}

}
