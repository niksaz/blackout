package ru.spbau.blackout.ingameui.settings;

import ru.spbau.blackout.units.Rpx;

/**
 * Contains some settings for displaying of this UI unit.
 * All getters and setters work with RPX.
 */
public class StickSettings {
    public static class Defaults {
        private Defaults() {}
        public static final float START_X = 0.6f;
        public static final float START_Y = 0.6f;
    }

    private int startX = Rpx.X.fromCm(Defaults.START_X);
    public void setStartX(int startX) { this.startX = startX; }
    public int getStartX() { return startX; }

    private int startY = Rpx.Y.fromCm(Defaults.START_Y);
    public void setStartY(int startY) { this.startY = startY; }
    public int getStartY() { return startY; }
}
