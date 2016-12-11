package ru.spbau.blackout.database;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("players")
public class PlayerEntity {

    @Id
    private ObjectId id;
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

    public String getName() {
        return name;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void changeGold(int delta) {
        setGold(getGold() + delta);
    }
}
