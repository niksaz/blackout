package ru.spbau.blackout.utils;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import static com.badlogic.gdx.math.MathUtils.PI;

public final class Utils {
    public static final double EPSILON = 1e-5f;

    public static boolean floatEq(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }

    public static boolean isZeroVec(Vector2 v) {
        return isZeroVec(v.x, v.y);
    }

    public static boolean isZeroVec(float x, float y) {
        return floatEq(x, 0) && floatEq(y, 0);
    }

    /**
     * Makes positive direction of Z axis as top (Y by default)
     */
    public static void fixTop(ModelInstance model) {
        // make Z axis as top (Y by default)
        model.transform.rotateRad(Vector3.X, PI / 2f);
    }

    public static float projectVec(Vector2 vec, Vector2 base) {
        return vec.len() * MathUtils.cos(vec.angleRad(base));
    }

    public static float sqr(float x) {
        return x * x;
    }
}
