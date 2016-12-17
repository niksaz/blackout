package ru.spbau.blackout.database;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.abilities.fireball.FireballAbility;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.shapescreators.CircleCreator;

/**
 * Representation of a player profile in a database.
 */
@Entity("players")
public class PlayerEntity implements Serializable {

    @Id
    private String name;
    private int gold;
    private byte[] serializedDefinition;

    public PlayerEntity() {
        this("");
    }

    public PlayerEntity(String name) {
        this.name = name;

        final Character.Definition initialCharacter = new Character.Definition(
                "models/wizard/wizard.g3db",
                new CircleCreator(0.6f),
                0, 0,
                new Ability[] { new FireballAbility(7) },
                200
        );
        try (
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOutput)
        ) {
            out.writeObject(initialCharacter);
            out.flush();
            serializedDefinition = byteOutput.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    public PlayerEntity(PlayerEntity other) {
        name = other.name;
        gold = other.gold;
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
