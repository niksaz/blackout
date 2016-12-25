package ru.spbau.blackout.ingameui.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.BooleanArray;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.ingameui.IngameUIObject;

import static ru.spbau.blackout.BlackoutGame.DIALOG_PADDING;

public class ExitButton extends IngameUIObject {

    public static final String TEXTURE_PATH = "images/exit-icon.png";
    private static final float SIZE = 100;
    private static final float START_X = HealthBar.START_X;
    private static final float START_Y = HealthBar.START_Y - SIZE - 50;

    @Override
    public void load(AssetManager assets) {
        assets.load(TEXTURE_PATH, Texture.class);
    }

    @Override
    public void doneLoading(AssetManager assets, Stage stage, Character character) {
        Image button = new Image(assets.get(TEXTURE_PATH, Texture.class));
        button.setSize(SIZE, SIZE);
        button.setPosition(START_X, START_Y);
        button.toFront();
        stage.addActor(button);
        button.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                new ConfirmationDialog().show(stage);
                return true;
            }
        });
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void dispose() {

    }

    private static class ConfirmationDialog extends Dialog {

        public ConfirmationDialog() {
            super("", BlackoutGame.get().assets().getDefaultSkin());
            setMovable(false);
            pad(DIALOG_PADDING);

            getContentTable().add("Are you sure that you want to exit?");

            button("Exit", true).padBottom(DIALOG_PADDING);
            button("Cancel", false).padBottom(DIALOG_PADDING);
        }

        @Override
        protected void result(Object object) {
            super.result(object);
            this.remove();
            if ((Boolean) object) {
                BlackoutGame.get().screenManager().disposeScreen();
            }
        }
    }
}
