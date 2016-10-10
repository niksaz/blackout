package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

import static ru.spbau.blackout.Units.cmToRpxX;
import static ru.spbau.blackout.Units.cmToRpxY;

public class Stick {
    private static final class MainImg {
        private static final float SIZE_CM = 2f;
        public static final int SIZE_RPX_X = cmToRpxX(SIZE_CM);
        public static final int SIZE_RPX_Y = cmToRpxY(SIZE_CM);

        private static final float START_X_CM = 0.3f;
        public static final float START_X_RPX = cmToRpxX(START_X_CM);

        private static final float START_Y_CM = 0.3f;
        public static final float START_Y_RPX = cmToRpxY(START_Y_CM);

        public static final float CENTER_X_RPX = START_X_RPX + SIZE_RPX_X / 2;
        public static final float CENTER_Y_RPX = START_Y_RPX + SIZE_RPX_Y / 2;

        public static final int MAX_AT_X = Math.round(0.8f * (SIZE_RPX_X / 2));
        public static final int MAX_AT_Y = Math.round(0.8f * (SIZE_RPX_Y / 2));

        public static final String PATH = "images/ingame_ui/stick_main.png";
    }

    private static final class TouchImg {
        private static final float SIZE_CM = 0.8f;
        public static final int SIZE_RPX_X = cmToRpxX(SIZE_CM);
        public static final int SIZE_RPX_Y = cmToRpxY(SIZE_CM);

        public static final String PATH = "images/ingame_ui/stick_touch.png";
    }

    private Vector2 velocity = new Vector2();

    public Stick(Stage stage) {
        Table table = new Table();

        table.left().bottom();
        table.padLeft(MainImg.START_X_RPX);
        table.padBottom(MainImg.START_Y_RPX);;

        Image mainImg = new Image(new Texture(MainImg.PATH));
        mainImg.setSize(MainImg.SIZE_RPX_X, MainImg.SIZE_RPX_Y);
        mainImg.addListener(new DragListener() {
            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                velocity.set(
                        Math.min((x - MainImg.CENTER_X_RPX) / MainImg.MAX_AT_X, 1),
                        Math.min((y - MainImg.CENTER_Y_RPX) / MainImg.MAX_AT_Y, 1)
                );
            }
        });

//        table.row().pad
        table.add(mainImg).size(MainImg.SIZE_RPX_X, MainImg.SIZE_RPX_Y); // FIXME

        stage.addActor(table);
    }
}
