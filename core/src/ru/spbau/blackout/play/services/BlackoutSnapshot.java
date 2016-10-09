package ru.spbau.blackout.play.services;

import java.io.Serializable;

public class BlackoutSnapshot implements Serializable {

    private int gold;

    public BlackoutSnapshot() {}

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void changeGold(int delta) {
        gold += delta;
    }

}
