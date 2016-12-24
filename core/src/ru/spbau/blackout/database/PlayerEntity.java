package ru.spbau.blackout.database;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.io.Serializable;

import ru.spbau.blackout.entities.Character;

/**
 * Representation of a player profile in a database.
 */
@Entity("players")
public class PlayerEntity implements Serializable {

    @Id
    private String name;
    private int gold;
    private byte[] serializedDefinition;

    /**
     * Used by Morphia to initialize objects extracted from the database.
     */
    public PlayerEntity() {
        this("", 0, null);
    }

    /**
     * Initialize the character of a new player.
     *
     * @param name name of a new player
     */
    public PlayerEntity(String name) {
        this(name, 0, Character.Definition.createSerializedDefaultCharacterDefinition());
    }

    /**
     * Shallow copy of the object.
     */
    public PlayerEntity(PlayerEntity other) {
        this(other.getName(), other.getGold(), other.getSerializedDefinition());
    }

    private PlayerEntity(String name, int gold, byte[] serializedDefinition) {
        this.name = name;
        this.gold = gold;
        this.serializedDefinition = serializedDefinition;
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

    public void changeGold(int delta) {
        gold += delta;
    }
}
