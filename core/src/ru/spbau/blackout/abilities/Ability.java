package ru.spbau.blackout.abilities;

import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.Hero;

public abstract class Ability {
    private int level = 0;

    public abstract String getIconPath();

    void setLevel(int newLevel) {
        this.level = newLevel;
    }

    void incLevel() {
        this.level += 1;
    }

    int getLevel() {
        return this.level;
    }
}
