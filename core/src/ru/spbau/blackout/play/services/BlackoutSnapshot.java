package ru.spbau.blackout.play.services;

import java.io.Serializable;

import ru.spbau.blackout.BlackoutGame;

public class BlackoutSnapshot implements Serializable {

    private int gold;
    private int rating;

    public BlackoutSnapshot() {
        gold = 0;
        rating = 0;
        saveState();
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
        saveState();
    }

    public void changeGold(int delta) {
        setGold(getGold() + delta);
    }

    private void saveState() {
        BlackoutGame.getInstance().getPlayServicesInCore().getPlayServices().saveSnapshot(this);
    }

}
