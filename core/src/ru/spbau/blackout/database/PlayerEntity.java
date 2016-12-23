package ru.spbau.blackout.database;

import com.badlogic.gdx.utils.ByteArray;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

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
        Character.Definition def = deserializeCharacterDefinition();
        System.out.println(def);
        System.out.println("LENGTH OF DEF ON SERVER IS " + serializedDefinition.length);
        System.out.println(Arrays.toString(serializedDefinition));
    }

    // just sets references to the same objects, because the other object won't be used later
    public PlayerEntity(PlayerEntity other) {
        name = other.name;
        gold = other.gold;
        serializedDefinition = other.serializedDefinition;
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
        try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(byteOutputStream)
        ) {
            out.writeObject(characterDefinition);
            out.flush();
            serializedDefinition = byteOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
}
