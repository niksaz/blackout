package ru.spbau.blackout.ingameui.settings;

import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.units.Rpx;

/**
 * Contains some settings for displaying of this UI object.
 * All getters and setters work with RPX.
 */
public class AbilityIconSettings {
    public static class Defaults {
        private Defaults() {}
        public static final float SIZE_CM = 1f;
    }

    private Ability ability;
    public Ability getAbility() { return this.ability; }
    public void setAbility(Ability ability) { this.ability = ability; }

    private int sizeX = Rpx.X.fromCm(Defaults.SIZE_CM);
    public int getSizeX() { return this.sizeX; }
    public void setSizeX(int sizeX) { this.sizeX = sizeX; }

    private int sizeY = Rpx.Y.fromCm(Defaults.SIZE_CM);
    public int getSizeY() { return this.sizeY; }
    public void setSizeY(int sizeY) { this.sizeY = sizeY; }

    private int startX;
    public int getStartX() { return this.startX; }
    public void setStartX(int startX) { this.startX = startX; }

    private int startY;
    public int getStartY() { return this.startY; }
    public void setStartY(int startY) { this.startY = startY; }
}
