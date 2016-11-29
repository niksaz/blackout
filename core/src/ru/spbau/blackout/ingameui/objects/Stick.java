package ru.spbau.blackout.ingameui.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.ingameui.IngameUIObject;
import ru.spbau.blackout.ingameui.settings.StickSettings;
import ru.spbau.blackout.network.AbstractServer;
import ru.spbau.blackout.units.Rpx;
import ru.spbau.blackout.units.Vpx;

import static ru.spbau.blackout.BlackoutGame.getWorldWidth;


/**
 * Class for stick which used to set character walking direction and speed.
 */
public class Stick extends IngameUIObject {
    /** current stick position (it is velocity for the unit) */
    private Vector2 velocity = new Vector2(0, 0);
    /** the controlled unit */
    private GameUnit unit;
    private Image touchImage;
    private final StickSettings settings;


    public Stick(AbstractServer server, StickSettings settings) {
        super(server);
        this.settings = settings;
    }


    @Override
    public void load(GameContext context) {
        AssetManager assets = context.assets().get();
        assets.load(TouchImg.IMAGE_PATH, Texture.class);
        assets.load(MainImg.IMAGE_PATH, Texture.class);
    }

    @Override
    public void doneLoading(GameContext context, Stage stage, Character character) {
        this.unit = character;
        AssetManager assets = context.assets().get();

        // touch image initialization
        this.touchImage = new Image(assets.get(TouchImg.IMAGE_PATH, Texture.class));
        this.touchImage.setSize(TouchImg.SIZE, TouchImg.SIZE);
        updateTouchPosition();
        stage.addActor(this.touchImage);

        // main image initialization
        // must go after touch image initialization to be in the foreground
        Image mainImg = new Image(assets.get(MainImg.IMAGE_PATH, Texture.class));
        mainImg.setSize(MainImg.SIZE, MainImg.SIZE);
        mainImg.setPosition(settings.getStart().x, settings.getStart().y);
        mainImg.addListener(this.new Listener());
        stage.addActor(mainImg);
    }

    @Override
    public void update(float deltaTime) { /*nothing*/ }


    private void updateTouchPosition() {
        Vector2 position = new Vector2(settings.getStart());
        position.add(MainImg.CENTER - TouchImg.CENTER, MainImg.CENTER - TouchImg.CENTER)
                .mulAdd(velocity, MainImg.MAX_AT);
        touchImage.setPosition(position.x, position.y);
    }


    private class Listener extends DragListener {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            touchMovedTo(x, y);
            return super.touchDown(event, x, y, pointer, button);
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            super.touchUp(event, x, y, pointer, button);
            touchMovedTo(MainImg.CENTER, MainImg.CENTER);
        }

        @Override
        public void drag(InputEvent event, float x, float y, int pointer) {
            super.drag(event, x, y, pointer);
            touchMovedTo(x, y);
        }


        private void touchMovedTo(float x, float y) {
            velocity.set((x - MainImg.CENTER) / MainImg.MAX_AT, (y - MainImg.CENTER) / MainImg.MAX_AT);

            float len = velocity.len();
            if (len > 1) {
                velocity.x /= len;
                velocity.y /= len;
            }

            unit.setSelfVelocity(velocity);
            updateTouchPosition();
            server.sendSelfVelocity(velocity);
        }
    }


    /** Some constants for main image (the font of the stick) */
    public static final class MainImg {
        private MainImg() {}

        public static final float SIZE = Math.min(Vpx.fromCm(3f), getWorldWidth() / 7);
        public static final float CENTER = SIZE / 2;  // because it is related to position
        public static final float MAX_AT = (SIZE - TouchImg.SIZE) / 2;

        public static final String IMAGE_PATH = "images/ingame_ui/stick_main.png";
    }


    /** Some constants for touch image. */
    public static final class TouchImg {
        private TouchImg() {}

        private static final float SIZE = MainImg.SIZE / 5;
        private static final float CENTER = SIZE / 2;

        public static final String IMAGE_PATH = "images/ingame_ui/stick_touch.png";
    }
}
