package ru.spbau.blackout.units;

import com.badlogic.gdx.Gdx;

/**
 * Convert to centimeters
 */
public class Cm {
    public static final class X {
        public static float fromRpx(int rpx) {
            return Math.round(rpx / Gdx.graphics.getPpcX());
        }
    }

    public static final class Y {
        public static int fromRpx(float cm) {
            return Math.round(cm / Gdx.graphics.getPpcY());
        }
    }
}
