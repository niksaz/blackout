package ru.spbau.blackout.units;

import com.badlogic.gdx.Gdx;


/**
 * Convert to real pixels
 */
public final class Rpx {
    public static int fromCm(float cm) {
        return Math.round(cm * Gdx.graphics.getPpcX());
    }

    public static float fromVpx(float vpx) {
        return Math.round(vpx * ru.spbau.blackout.units.Vpx.getRpxPerVpx());
    }
}
