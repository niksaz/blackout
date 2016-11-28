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
        this.touchImage.setSize(TouchImg.X.SIZE, TouchImg.Y.SIZE);
        updateTouchPosition();
        stage.addActor(this.touchImage);

        // main image initialization
        // must go after touch image initialization to be in the foreground
        Image mainImg = new Image(assets.get(MainImg.IMAGE_PATH, Texture.class));
        mainImg.setSize(MainImg.X.SIZE, MainImg.Y.SIZE);
        mainImg.setPosition(settings.getStartX(), settings.getStartY());
        mainImg.addListener(this.new Listener());
        stage.addActor(mainImg);
    }

    @Override
    public void update(float deltaTime) { /*nothing*/ }


    private void updateTouchPosition() {
        touchImage.setPosition(
                settings.getStartX() + MainImg.X.CENTER  // move (0,0) to the center of mainImg
                        - TouchImg.X.CENTER                 // move pivot to the center of image
                        + velocity.x * MainImg.X.MAX_AT,
                settings.getStartY() + MainImg.Y.CENTER  // move (0,0) to the center of mainImg
                        - TouchImg.Y.CENTER                 // move pivot to the center of image
                        + velocity.y * MainImg.Y.MAX_AT
        );
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
            touchMovedTo(MainImg.X.CENTER, MainImg.Y.CENTER);
        }

        @Override
        public void drag(InputEvent event, float x, float y, int pointer) {
            super.drag(event, x, y, pointer);
            touchMovedTo(x, y);
        }

        private void touchMovedTo(float x, float y) {
            velocity.set(
                    (x - MainImg.X.CENTER) / MainImg.X.MAX_AT,
                    (y - MainImg.Y.CENTER) / MainImg.Y.MAX_AT
            );

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

        public static final class InCentimeters {
            private InCentimeters() {}
            private static final float SIZE = 1.5f;
        }

        public static final class X {
            private X() {}
            public static final int SIZE = Rpx.X.fromCm(InCentimeters.SIZE);
            public static final float MAX_AT = (SIZE - TouchImg.X.SIZE) / 2;
            public static final float CENTER = SIZE / 2;
        }

        public static final class Y {
            private Y() {}
            public static final int SIZE = Rpx.Y.fromCm(InCentimeters.SIZE);
            public static final float MAX_AT = (SIZE - TouchImg.Y.SIZE) / 2;
            public static final float CENTER = SIZE / 2;
        }

        public static final String IMAGE_PATH = "images/ingame_ui/stick_main.png";
    }


    /** Some constants for touch image. */
    public static final class TouchImg {
        private TouchImg() {}

        public static final class InCentimeters {
            private InCentimeters() {}
            private static final float SIZE = 0.3f;
        }

        public static final class X {
            private X() {}
            private static final float SIZE = Rpx.X.fromCm(InCentimeters.SIZE);
            private static final float CENTER = Rpx.X.fromCm(InCentimeters.SIZE / 2);
        }

        public static final class Y {
            private Y() {}
            private static final float SIZE = Rpx.Y.fromCm(InCentimeters.SIZE);
            private static final float CENTER = Rpx.Y.fromCm(InCentimeters.SIZE / 2);
        }

        public static final String IMAGE_PATH = "images/ingame_ui/stick_touch.png";
    }
}
