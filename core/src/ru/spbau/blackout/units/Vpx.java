package ru.spbau.blackout.units;

import com.badlogic.gdx.Gdx;

import static ru.spbau.blackout.BlackoutGame.getWorldWidth;


/**
 * Convert to virtual world pixels
 */
public class Vpx {
    /** For lazy initialization, to avoid NullPointerException */
    private final static class ConstHolder {
        public static final float RPX_PER_VPX = (float)Gdx.graphics.getWidth() / (float)getWorldWidth();
    }
    public static float getRpxPerVpx() {  // FIXME
        return ConstHolder.RPX_PER_VPX;
    }

    public static int fromRpx(int rpx) {
        return Math.round(rpx / getRpxPerVpx());
    }

    public static int fromCm(float cm) {
        return fromRpx(Rpx.fromCm(cm));
    }
}
