package ru.spbau.blackout.units;

import com.badlogic.gdx.Gdx;

import static ru.spbau.blackout.BlackoutGame.VIRTUAL_WORLD_HEIGHT;
import static ru.spbau.blackout.BlackoutGame.VIRTUAL_WORLD_WIDTH;

/**
 * Convert to virtual world pixels
 */
public class Vpx {
    public static final class X {
        public static float getRpxPerVpx() {
            return (float)Gdx.graphics.getWidth() / (float)VIRTUAL_WORLD_WIDTH;
        }

        public static int fromRpx(int rpx) {
            return Math.round(rpx / getRpxPerVpx());
        }

        public static int fromCm(float cm) {
            return fromRpx(Rpx.X.fromCm(cm));
        }
    }

    public static final class Y {
        public static float getRpxPerVpx() {
            return (float)Gdx.graphics.getHeight() / (float)VIRTUAL_WORLD_HEIGHT;
        }

        public static int fromRpx(int rpx) {
            return Math.round(rpx / getRpxPerVpx());
        }

        public static int fromCm(float cm) {
            return fromRpx(Rpx.Y.fromCm(cm));
        }
    }
}
