package ru.spbau.blackout.ingameui.settings;

import java.io.Serializable;

/**
 * Settings for all in-game user interface.
 */
public class IngameUISettings implements Serializable {

    public StickSettings stickSettings = new StickSettings();
    public AbilityIconSettings[] abilities;

    // FIXME: mainly for test
    public IngameUISettings(AbilityIconSettings[] abilities) {
        this.abilities = abilities;
    }
}