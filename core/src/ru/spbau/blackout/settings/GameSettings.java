package ru.spbau.blackout.settings;

import java.io.Serializable;

import ru.spbau.blackout.ingameui.settings.IngameUISettings;


public class GameSettings implements Serializable {
    public final IngameUISettings ui;
    public float battleMusicVolume = 0.5f;
    public float effectsVolume = 0.5f;

    public GameSettings(IngameUISettings ui) {
        this.ui = ui;
    }
}
