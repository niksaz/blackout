package ru.spbau.blackout;

import com.badlogic.gdx.math.Vector2;

public final class Utils {
    public static final double EPS = 1e-5f;

    public static boolean floatEq(double a, double b) {
        return Math.abs(a - b) < EPS;
    }

    public static boolean isZeroVec(Vector2 v) {
        return isZeroVec(v.x, v.y);
    }

    public static boolean isZeroVec(float x, float y) {
        return floatEq(x, 0) && floatEq(y, 0);
    }
}
