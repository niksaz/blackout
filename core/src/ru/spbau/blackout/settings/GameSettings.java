package ru.spbau.blackout.settings;

import java.io.Serializable;

import ru.spbau.blackout.utils.Serializer;

public class GameSettings implements Serializable {

    private static final long serialVersionUID = 1000000000L;

    public static final float MUSIC_MAX_VOLUME = 0.4f;
    public static final float SOUND_MAX_VOLUME = 1.0f;

    public float musicVolume = 0.5f;
    public float soundVolume = 0.5f;


    public byte[] serializeToByteArray() {
        return Serializer.serializeToByteArray(this);
    }

    public static GameSettings deserializeFromByteArray(byte[] byteRepresentation) {
        return (GameSettings) Serializer.deserializeFromByteArray(byteRepresentation);
    }

    public static GameSettings createDefaultGameSettings() {
        return new GameSettings();
    }

    public static byte[] createSerializedDefaultGameSettings() {
        return createDefaultGameSettings().serializeToByteArray();
    }
}
