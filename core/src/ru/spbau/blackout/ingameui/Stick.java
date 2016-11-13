package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

import java.util.function.Function;

import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.units.Rpx;

public class Stick extends DragListener {
    public static final class MainImg {
        public static final class InCentimeters {
            private static final float SIZE = 1.5f;
        }

        public static final class X {
            public static final int SIZE = Rpx.X.fromCm(InCentimeters.SIZE);
            public static final float MAX_AT = (SIZE - TouchImg.X.SIZE) / 2;
            public static final float CENTER = SIZE / 2;
        }

        public static final class Y {
            public static final int SIZE = Rpx.Y.fromCm(InCentimeters.SIZE);
            public static final float MAX_AT = (SIZE - TouchImg.Y.SIZE) / 2;
            public static final float CENTER = SIZE / 2;
        }

        public static final String IMAGE_PATH = "images/ingame_ui/stick_main.png";
    }

    public static final class TouchImg {
        public static final class InCentimeters {
            private static final float SIZE = 0.3f;
        }

        public static final class X {
            private static final float SIZE = Rpx.X.fromCm(InCentimeters.SIZE);
            private static final float CENTER = Rpx.X.fromCm(InCentimeters.SIZE / 2);
        }

        public static final class Y {
            private static final float SIZE = Rpx.Y.fromCm(InCentimeters.SIZE);
            private static final float CENTER = Rpx.Y.fromCm(InCentimeters.SIZE / 2);
        }

        public static final String IMAGE_PATH = "images/ingame_ui/stick_touch.png";
    }

    private Vector2 velocity = new Vector2(0, 0);
    private GameUnit object;
    private Image touchImage;
    private Settings settings;

    public Stick(Settings settings) {
        this.settings = settings;
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        movedTo(x, y);
        return super.touchDown(event, x, y, pointer, button);
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        super.touchUp(event, x, y, pointer, button);
        movedTo(MainImg.X.CENTER, MainImg.Y.CENTER);
    }

    @Override
    public void drag(InputEvent event, float x, float y, int pointer) {
        super.drag(event, x, y, pointer);
        movedTo(x, y);
    }

    public void load(AssetManager assets) {
        assets.load(TouchImg.IMAGE_PATH, Texture.class);
        assets.load(MainImg.IMAGE_PATH, Texture.class);
    }

    public void doneLoading(AssetManager assets, Stage stage, GameUnit object) {
        this.object = object;

        // touch image initialization
        touchImage = new Image(assets.get(TouchImg.IMAGE_PATH, Texture.class));
        touchImage.setSize(TouchImg.X.SIZE, TouchImg.Y.SIZE);
        updateTouchPosition();
        stage.addActor(touchImage);

        // main image initialization
        // must go after touch image initialization to be in the foreground
        Image mainImg = new Image(assets.get(MainImg.IMAGE_PATH, Texture.class));
        mainImg.setSize(MainImg.X.SIZE, MainImg.Y.SIZE);
        mainImg.setPosition(settings.startX, settings.startY);
        mainImg.addListener(this);
        stage.addActor(mainImg);
    }

    private void movedTo(float x, float y) {
        velocity.set(
                (x - MainImg.X.CENTER) / MainImg.X.MAX_AT,
                (y - MainImg.Y.CENTER) / MainImg.Y.MAX_AT
        );

        float len = velocity.len();
        if (len > 1) {
            velocity.x /= len;
            velocity.y /= len;
        }

        object.setSelfVelocity(velocity);
        updateTouchPosition();
    }

    private void updateTouchPosition() {
        touchImage.setPosition(
                settings.startX + MainImg.X.CENTER  // move (0,0) to the center of mainImg
                        - TouchImg.X.CENTER                 // move pivot to the center of image
                        + velocity.x * MainImg.X.MAX_AT,
                settings.startY + MainImg.Y.CENTER  // move (0,0) to the center of mainImg
                        - TouchImg.Y.CENTER                 // move pivot to the center of image
                        + velocity.y * MainImg.Y.MAX_AT
        );
    }

    public static class Settings {
        public static class Defaults {
            public static final float START_X = 0.6f;
            public static final float START_Y = 0.6f;
        }

        public int startX = Rpx.X.fromCm(Defaults.START_X);
        public int startY = Rpx.Y.fromCm(Defaults.START_Y);
    }
}
