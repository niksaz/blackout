package ru.spbau.blackout.ingameui.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.ingameui.IngameUIObject;
import ru.spbau.blackout.network.UIServer;
import ru.spbau.blackout.units.Vpx;
import ru.spbau.blackout.utils.Textures;

import static ru.spbau.blackout.BlackoutGame.getWorldWidth;


/**
 * Class for stick which used to set character walking direction and speed.
 */
public abstract class AbstractStick extends IngameUIObject {
    public static final float MAIN_IMAGE_SIZE = Math.min(Vpx.fromCm(3f), getWorldWidth() / 7);
    public static final float MAIN_IMAGE_CENTER = MAIN_IMAGE_SIZE / 2;  // because it is related to touchPos
    public static final String MAIN_IMAGE_PATH = "images/stick/stick_main.png";

    public static final float START_X = 100;
    public static final float START_Y = 100;

    public static final float TOUCH_IMAGE_SIZE = MAIN_IMAGE_SIZE / 5;
    public static final float TOUCH_IMAGE_CENTER = TOUCH_IMAGE_SIZE / 2;
    public static final String TOUCH_IMAGE_PATH = "images/stick/stick_touch.png";

    public static final float MAX_AT = (MAIN_IMAGE_SIZE - TOUCH_IMAGE_SIZE) / 2;


    /** current stick touchPos (it is touchPos for the unit) */
    protected final Vector2 touchPos = new Vector2(0, 0);
    private Image touchImage;
    protected final UIServer server;


    public AbstractStick(UIServer server) {
        this.server = server;
    }


    @Override
    public void load(GameContext context) {
        Textures.loadAA(MAIN_IMAGE_PATH, context.getAssets());
        Textures.loadAA(TOUCH_IMAGE_PATH, context.getAssets());
    }

    @Override
    public void doneLoading(GameContext context, Stage stage) {
        // main image initialization
        Image mainImg = new Image(context.getAssets().get(MAIN_IMAGE_PATH, Texture.class));
        mainImg.setSize(MAIN_IMAGE_SIZE, MAIN_IMAGE_SIZE);
        mainImg.setPosition(START_X, START_Y);
        mainImg.addListener(this.new Listener());
        stage.addActor(mainImg);

        // touch image initialization
        this.touchImage = new Image(context.getAssets().get(TOUCH_IMAGE_PATH, Texture.class));
        this.touchImage.setSize(TOUCH_IMAGE_SIZE, TOUCH_IMAGE_SIZE);
        updateTouchPosition();

        stage.addActor(this.touchImage);

        // in order to be able to handle input by main image
        this.touchImage.toBack();
        mainImg.toFront();
    }

    @Override
    public void update(float delta) { /*nothing*/ }


    private void updateTouchPosition() {
        Vector2 position = new Vector2(START_X, START_Y);
        position.add(MAIN_IMAGE_CENTER - TOUCH_IMAGE_CENTER, MAIN_IMAGE_CENTER - TOUCH_IMAGE_CENTER)
                .mulAdd(this.touchPos, MAX_AT);
        touchImage.setPosition(position.x, position.y);
    }

    @Override
    public void dispose() {
        // TODO
    }

    protected final void touchMovedTo(float x, float y) {
        touchPos.set((x - MAIN_IMAGE_CENTER) / MAX_AT, (y - MAIN_IMAGE_CENTER) / MAX_AT);

        float len = touchPos.len();
        if (len > 1) {
            touchPos.x /= len;
            touchPos.y /= len;
        }

        updateTouchPosition();
        onTouchMove();
    }

    protected void onTouchMove() {}

    private class Listener extends DragListener {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            touchMovedTo(x, y);
            return super.touchDown(event, x, y, pointer, button);
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            super.touchUp(event, x, y, pointer, button);
            touchMovedTo(MAIN_IMAGE_CENTER, MAIN_IMAGE_CENTER);
        }

        @Override
        public void drag(InputEvent event, float x, float y, int pointer) {
            super.drag(event, x, y, pointer);
            touchMovedTo(x, y);
        }
    }
}
