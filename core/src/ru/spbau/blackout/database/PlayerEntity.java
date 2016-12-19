package ru.spbau.blackout.database;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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
        this.gold = 0;
        final Character.Definition initialCharacter = new Character.Definition(
                "models/wizard/wizard.g3db",
                new CircleCreator(0.6f),
                new Ability[] { new FireballAbility(1) },
                200
        );
        setSerializedCharacterDefinition(initialCharacter);
    }

    public PlayerEntity(PlayerEntity other) {
        name = other.name;
        gold = other.gold;
        final byte[] otherSerializedDefinition = other.getSerializedDefinition();
        serializedDefinition = new byte[otherSerializedDefinition.length];
        System.arraycopy(otherSerializedDefinition, 0, serializedDefinition, 0, otherSerializedDefinition.length);
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

    public Character.Definition deserializeCharacterDefinition() {
        Character.Definition characterDefinition;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedDefinition);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)
        ) {
            characterDefinition = (Character.Definition) objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
        return characterDefinition;
    }

    public void setSerializedCharacterDefinition(Character.Definition characterDefinition) {
        try (ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(byteOutput)
        ) {
            out.writeObject(characterDefinition);
            out.flush();
            serializedDefinition = byteOutput.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
}
