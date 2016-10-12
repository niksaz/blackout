package ru.spbau.blackout.play.services;

import java.io.Serializable;

import ru.spbau.blackout.BlackoutGame;

public class BlackoutSnapshot implements Serializable {

    private int gold;

    public BlackoutSnapshot() {}

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
        saveState();
    }

    public void changeGold(int delta) {
        gold += delta;
        saveState();
    }

    private void saveState() {
        BlackoutGame.playServices.saveSnapshot(this);
    }

}
