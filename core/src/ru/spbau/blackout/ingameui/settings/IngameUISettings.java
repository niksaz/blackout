package ru.spbau.blackout.ingameui.settings;


/**
 * Settings for all in-game user interface.
 */
public class IngameUISettings {
    public StickSettings stickSettings = new StickSettings();
    public AbilityIconSettings[] abilities;

    // FIXME: mainly for test
    public IngameUISettings(AbilityIconSettings[] abilities) {
        this.abilities = abilities;
    }
}