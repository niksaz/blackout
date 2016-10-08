package ru.spbau.blackout.utils;

import com.badlogic.gdx.utils.Array;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.screens.BlackoutScreen;

public class ScreenManager {

    private static ScreenManager screenManager;
    private final Array<BlackoutScreen> screens = new Array<BlackoutScreen>();
    private BlackoutGame game;

    private ScreenManager() {}

    public static ScreenManager getInstance() {
        if (screenManager == null) {
            screenManager = new ScreenManager();
        }
        return screenManager;
    }

    public void initialize(BlackoutGame game) {
        this.game = game;
    }

    public void setScreen(BlackoutScreen screen) {
        screens.add(screen);
        game.setScreen(screen);
    }

    public void disposeScreen() {
        disposeScreens(1);
    }

    public void disposeScreens(int number) {
        for (int i = 0; i < number; i++) {
            if (screens.size == 1) {
                break;
            }
            screens.pop().dispose();
        }
        game.setScreen(screens.peek());
    }

}
