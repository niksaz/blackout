package ru.spbau.blackout.settings;

import java.io.Serializable;

import ru.spbau.blackout.ingameui.settings.AbilityIconSettings;
import ru.spbau.blackout.ingameui.settings.IngameUISettings;
import ru.spbau.blackout.utils.Serializer;

public class GameSettings implements Serializable {

    public static final float MAX_SOUND_VOLUME = 1f;
    public static final float MUSIC_MAX_VOLUME = 0.4f;

    public final IngameUISettings ui;
    public float musicVolume = 0.5f;
    public float soundVolume = 0.5f;

    public GameSettings(IngameUISettings ui) {
        this.ui = ui;
    }

    public byte[] serializeToByteArray() {
        return Serializer.serializeToByteArray(this);
    }

    public static GameSettings deserializeFromByteArray(byte[] byteRepresentation) {
        return (GameSettings) Serializer.deserializeFromByteArray(byteRepresentation);
    }

    public static GameSettings createDefaultGameSettings() {
        final AbilityIconSettings firstIconSettings = new AbilityIconSettings(0);
        final IngameUISettings uiSettings = new IngameUISettings(new AbilityIconSettings[] { firstIconSettings });
        return new GameSettings(uiSettings);
    }

    public static byte[] createSerializedDefaultGameSettings() {
        return createDefaultGameSettings().serializeToByteArray();
    }
}
