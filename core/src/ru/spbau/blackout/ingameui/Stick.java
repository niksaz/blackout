package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

import ru.spbau.blackout.units.Rpx;
import ru.spbau.blackout.units.Vpx;

public class Stick {
    /**
     * All constants in centimeters.
     */
    private static final class MainImg {
        public static final float SIZE = 2f;
        public static final float START_X = 0.3f;
        private static final float START_Y = 0.3f;

        private static final float MAX_FACTOR = 0.8f;
        public static final float MAX_AT_X = MAX_FACTOR * (SIZE / 2);
        public static final float MAX_AT_Y = MAX_FACTOR * (SIZE / 2);

        public static final String IMAGE_PATH = "images/ingame_ui/stick_main.png";
    }

    private static final class TouchImg {
        private static final float SIZE = 0.8f;

        public static final String IMAGE_PATH = "images/ingame_ui/stick_touch.png";
    }

    private Vector2 velocity = new Vector2();

    public Stick(Stage stage) {
        Table table = new Table();

        table.left().bottom();
        table.padLeft(Rpx.X.fromCm(MainImg.START_X));
        table.padBottom(Rpx.Y.fromCm(MainImg.START_Y));

        Image mainImg = new Image(new Texture(MainImg.IMAGE_PATH));
        mainImg.setSize(Rpx.X.fromCm(MainImg.SIZE), Rpx.Y.fromCm(MainImg.SIZE));
        mainImg.addListener(new DragListener() {
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                velocity.set(
                        (x - Rpx.X.fromCm(MainImg.SIZE / 2)) / Rpx.X.fromCm(MainImg.MAX_AT_X),
                        (y - Rpx.Y.fromCm(MainImg.SIZE / 2)) / Rpx.Y.fromCm(MainImg.MAX_AT_Y)
                );

                if (velocity.x < -1) {
                    velocity.x = -1;
                }
                if (velocity.x > 1) {
                    velocity.x = 1;
                }
                if (velocity.y < -1) {
                    velocity.y = -1;
                }
                if (velocity.y > 1) {
                    velocity.y = 1;
                }

                Gdx.app.error("MyTag", velocity.x + " " + velocity.y);
            }
        });

        table.add(mainImg).size(Rpx.X.fromCm(MainImg.SIZE), Rpx.Y.fromCm(MainImg.SIZE));

        stage.addActor(table);
    }
}
