package ru.spbau.blackout.database;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.io.Serializable;

import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.settings.GameSettings;

/**
 * Representation of a player profile in a database.
 */
@Entity("players")
public class PlayerProfile implements Serializable {

    @Id
    private final String name;
    private int currentCoins;
    private int earnedCoins;
    private double rating;
    private byte[] serializedDefinition;
    private volatile byte[] serializedSettings;

    /**
     * Used by Morphia to initialize objects extracted from the database.
     */
    public PlayerProfile() {
        this("", 0, 0, 0, null, null);
    }

    /**
     * Initialize the character of a new player.
     *
     * @param name name of a new player
     */
    public PlayerProfile(String name) {
        this(name, 0, 0, 1000,
             Character.Definition.createSerializedDefaultCharacterDefinition(),
             GameSettings.createSerializedDefaultGameSettings());
    }

    /**
     * Shallow copy of the object.
     */
    public PlayerProfile(PlayerProfile other) {
        this(other.getName(), other.getCurrentCoins(), other.getEarnedCoins(), other.getRating(),
             other.getSerializedDefinition(), other.getSerializedSettings());
    }

    private PlayerProfile(String name, int currentCoins, int earnedCoins, double rating,
                          byte[] serializedDefinition, byte[] serializedSettings) {
        this.name = name;
        this.currentCoins = currentCoins;
        this.earnedCoins = earnedCoins;
        this.rating = rating;
        this.serializedDefinition = serializedDefinition;
        this.serializedSettings = serializedSettings;
    }

    public String getName() {
        return name;
    }

    public int getCurrentCoins() {
        return currentCoins;
    }

    public double getRating() {
        return rating;
    }

    public int getEarnedCoins() {
        return earnedCoins;
    }

    public byte[] getSerializedDefinition() {
        return serializedDefinition;
    }

    public byte[] getSerializedSettings() {
        return serializedSettings;
    }

    public Character.Definition getCharacterDefinition() {
        return Character.Definition.deserializeFromByteArray(serializedDefinition);
    }

    public GameSettings getGameSettings() {
        return GameSettings.deserializeFromByteArray(serializedSettings);
    }

    public void setGameSettings(GameSettings gameSettings) {
        serializedSettings = gameSettings.serializeToByteArray();
    }
}
