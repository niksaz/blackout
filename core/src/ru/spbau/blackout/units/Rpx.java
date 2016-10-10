package ru.spbau.blackout.units;

import com.badlogic.gdx.Gdx;

/**
 * Convert to real pixels
 */
public final class Rpx {
    public static final class X {
        public static int fromCm(float cm) {
            return Math.round(cm * Gdx.graphics.getPpcX());
        }
    }

    public static final class Y {
        public static int fromCm(float cm) {
            return Math.round(cm * Gdx.graphics.getPpcY());
        }
    }
}
