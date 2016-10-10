package ru.spbau.blackout;

import com.badlogic.gdx.Gdx;

/**
 * rpx - real pixels
 * cm - centimeters
 * vpx - virtual world pixels
 */
public final class Units {
    public static int cmToRpxX(float cm) {
        return Math.round(cm * Gdx.graphics.getPpcX());
    }

    public static int cmToRpxY(float cm) {
        return Math.round(cm * Gdx.graphics.getPpcY());
    }

    public static float rpxToCmX(int rpx) {
        return (float)rpx / Gdx.graphics.getPpcX();
    }

    public static float rpxToCmY(int rpx) {
        return (float)rpx / Gdx.graphics.getPpcY();
    }


    // TODO:
    // public static int rpxToVirtualX(int real) {
    // }
}
