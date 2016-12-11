package ru.spbau.blackout.database;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import java.io.Serializable;

@Entity("players")
public class PlayerEntity implements Serializable {

    @Id
    private String name;
    private int gold;

    public PlayerEntity() {
        this("");
    }

    public PlayerEntity(String name) {
        this(name, 0);
    }

    public PlayerEntity(String name, int gold) {
        this.name = name;
        this.gold = gold;
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

    public void changeGold(int delta) {
        gold += delta;
    }
}
