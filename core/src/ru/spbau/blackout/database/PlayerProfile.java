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
    private int gold;
    private byte[] serializedDefinition;
    private byte[] serializedSettings;

    /**
     * Used by Morphia to initialize objects extracted from the database.
     */
    public PlayerProfile() {
        this("", 0, null, null);
    }

    /**
     * Initialize the character of a new player.
     *
     * @param name name of a new player
     */
    public PlayerProfile(String name) {
        this(name,
             0,
             Character.Definition.createSerializedDefaultCharacterDefinition(),
             GameSettings.createSerializedDefaultGameSettings());
    }

    /**
     * Shallow copy of the object.
     */
    public PlayerProfile(PlayerProfile other) {
        this(other.getName(), other.getGold(), other.getSerializedDefinition(), other.getSerializedSettings());
    }

    private PlayerProfile(String name, int gold, byte[] serializedDefinition, byte[] serializedSettings) {
        this.name = name;
        this.gold = gold;
        this.serializedDefinition = serializedDefinition;
        this.serializedSettings = serializedSettings;
    }

    public String getName() {
        return name;
    }

    public int getGold() {
        return gold;
    }

    public byte[] getSerializedDefinition() {
        return serializedDefinition;
    }

    public byte[] getSerializedSettings() {
        return serializedSettings;
    }

    public Character.Definition getDeserializedCharacterDefinition() {
        return Character.Definition.deserializeFromByteArray(serializedDefinition);
    }
}
