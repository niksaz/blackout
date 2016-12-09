package ru.spbau.blackout.units;

import com.badlogic.gdx.Gdx;

/**
 * Convert to centimeters
 */
public class Cm {
    public static float fromRpx(int rpx) {
        return Math.round(rpx / Gdx.graphics.getPpcX());
    }
}
