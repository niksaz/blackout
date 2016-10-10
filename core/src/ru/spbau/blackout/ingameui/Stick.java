package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.screens.GameScreen;
import ru.spbau.blackout.units.Rpx;

public class Stick extends DragListener {
    /**
     * All constants in centimeters.
     */
    public static final class MainImg {
        public static final float SIZE = 2f;
        public static final float START_X = 0.3f;
        private static final float START_Y = 0.3f;

        private static final float MAX_FACTOR = 0.8f;
        public static final float MAX_AT_X = MAX_FACTOR * (SIZE / 2);
        public static final float MAX_AT_Y = MAX_FACTOR * (SIZE / 2);

        public static final String IMAGE_PATH = "images/ingame_ui/stick_main.png";
    }

    public static final class TouchImg {
        private static final float SIZE = 0.8f;

        public static final String IMAGE_PATH = "images/ingame_ui/stick_touch.png";
    }

    private Vector2 velocity = new Vector2();
    private final Hero hero;

    public Stick(Stage stage, Hero hero) {
        this.hero = hero;
        final Table table = new Table();

        table.left().bottom();
        table.padLeft(Rpx.X.fromCm(MainImg.START_X));
        table.padBottom(Rpx.Y.fromCm(MainImg.START_Y));

        Image mainImg = new Image(new Texture(MainImg.IMAGE_PATH));
        mainImg.setSize(Rpx.X.fromCm(MainImg.SIZE), Rpx.Y.fromCm(MainImg.SIZE));
        mainImg.addListener(this);

        table.add(mainImg).size(Rpx.X.fromCm(MainImg.SIZE), Rpx.Y.fromCm(MainImg.SIZE));

        stage.addActor(table);
    }

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        update(x, y);
        return super.touchDown(event, x, y, pointer, button);
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
        updateShifted(0, 0);
        super.touchUp(event, x, y, pointer, button);
    }

    @Override
    public void drag(InputEvent event, float x, float y, int pointer) {
        update(x, y);
    }

    private void update(float x, float y) {
        updateShifted(
                x - Rpx.X.fromCm(MainImg.SIZE / 2),
                y - Rpx.Y.fromCm(MainImg.SIZE / 2)
        );
    }

    private void updateShifted(float x, float y) {
        velocity.set(
                x / Rpx.X.fromCm(MainImg.MAX_AT_X),
                y / Rpx.Y.fromCm(MainImg.MAX_AT_Y)
        );

        float len = velocity.len();
        if (len > 1) {
            velocity.x /= len;
            velocity.y /= len;
        }
        hero.setSelfVelocity(velocity);
    }
}
