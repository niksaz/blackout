package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.units.Cm;
import ru.spbau.blackout.units.Rpx;

public class Stick extends DragListener {
    public static final class MainImg {
        public static final class CmC {
            public static final float SIZE = 2f;
            public static final float START_X = 0.3f;
            private static final float START_Y = 0.3f;
        }

        public static final class RpxC {
            public static final class X {
                public static final int SIZE = Rpx.X.fromCm(CmC.SIZE);
                private static final int START = Rpx.X.fromCm(CmC.START_X);
                public static final float MAX_AT = MAX_FACTOR * (SIZE / 2);
                public static final float CENTER = SIZE / 2;
            }

            public static final class Y {
                public static final int SIZE = Rpx.Y.fromCm(CmC.SIZE);
                private static final int START = Rpx.Y.fromCm(CmC.START_Y);
                public static final float MAX_AT = MAX_FACTOR * (SIZE / 2);
                public static final float CENTER = SIZE / 2;
            }
        }

        private static final float MAX_FACTOR = 0.8f;
        public static final String IMAGE_PATH = "images/ingame_ui/stick_main.png";
    }

    public static final class TouchImg {
        public static final class CmC {
            private static final float SIZE = 0.3f;
        }

        public static final class RpxC {
            public static final class X {
                private static final float SIZE = Rpx.X.fromCm(CmC.SIZE);
                private static final float CENTER = Rpx.X.fromCm(CmC.SIZE / 2);
            }
            public static final class Y {
                private static final float SIZE = Rpx.Y.fromCm(CmC.SIZE);
                private static final float CENTER = Rpx.Y.fromCm(CmC.SIZE / 2);
            }
        }


        public static final String IMAGE_PATH = "images/ingame_ui/stick_touch.png";
    }

    private final Vector2 velocity = new Vector2();
    private final Hero hero;
    private final Image touchImage;

    public Stick(Stage stage, Hero hero) {
        this.hero = hero;

        touchImage = new Image(new Texture(TouchImg.IMAGE_PATH));
        touchImage.setSize(TouchImg.RpxC.X.SIZE, TouchImg.RpxC.Y.SIZE);
        touchImage.setPosition(
                MainImg.RpxC.X.CENTER + MainImg.RpxC.X.START,
                MainImg.RpxC.Y.CENTER + MainImg.RpxC.Y.START
        );
        stage.addActor(touchImage);

        Image mainImg = new Image(new Texture(MainImg.IMAGE_PATH));
        mainImg.setSize(MainImg.RpxC.X.SIZE, MainImg.RpxC.Y.SIZE);
        mainImg.setPosition(MainImg.RpxC.X.START, MainImg.RpxC.Y.START);
        mainImg.addListener(this);
        stage.addActor(mainImg);
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        movedTo(x, y);
        return super.touchDown(event, x, y, pointer, button);
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        super.touchUp(event, x, y, pointer, button);
        movedTo(MainImg.RpxC.X.CENTER, MainImg.RpxC.Y.CENTER);
    }

    @Override
    public void drag(InputEvent event, float x, float y, int pointer) {
        super.drag(event, x, y, pointer);
        movedTo(x, y);
    }

    private void movedTo(float x, float y) {
        touchImage.setPosition(
                x + MainImg.RpxC.X.START - TouchImg.RpxC.X.CENTER,
                y + MainImg.RpxC.Y.START - TouchImg.RpxC.Y.CENTER
        );

        velocity.set(
                (x - MainImg.RpxC.X.CENTER) / MainImg.RpxC.X.MAX_AT,
                (y - MainImg.RpxC.Y.CENTER) / MainImg.RpxC.Y.MAX_AT
        );

        float len = velocity.len();
        if (len > 1) {
            velocity.x /= len;
            velocity.y /= len;
        }

        // convert from (x,y) plane to (x, z) plane
        velocity.y = -velocity.y;

        hero.setSelfVelocity(velocity);
    }
}
