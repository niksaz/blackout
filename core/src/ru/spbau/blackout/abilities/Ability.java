package ru.spbau.blackout.abilities;

import ru.spbau.blackout.entities.GameUnit;

public abstract class Ability {
    private final int level;

    public Ability(int level) {
        this.level = level;
    }

    public abstract void onCastStart(GameUnit unit);
    public abstract void inCast(GameUnit unit, float deltaTime);
    public abstract void onCastEnd(GameUnit unit);
    public abstract String iconPath();
}
