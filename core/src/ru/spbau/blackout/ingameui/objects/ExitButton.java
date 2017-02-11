package ru.spbau.blackout.ingameui.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.ingameui.IngameUIObject;
import ru.spbau.blackout.worlds.ClientGameWorld;
import ru.spbau.blackout.worlds.GameWorld;

import static ru.spbau.blackout.BlackoutGame.DIALOG_PADDING;

public class ExitButton extends IngameUIObject {

    public static final String TEXTURE_PATH = "images/exit-icon.png";
    private static final float SIZE = 100;
    private static final float START_X = HealthBar.START_X;
    private static final float START_Y = HealthBar.START_Y - SIZE - 50;

    private GameContext context;

    public ExitButton(Stage stage) {
        super(stage);
    }

    @Override
    public void load(GameContext context) {
        this.context = context;
        context.getAssets().load(TEXTURE_PATH, Texture.class);
    }

    @Override
    public void doneLoading(GameContext context) {
        Image button = new Image(context.getAssets().get(TEXTURE_PATH, Texture.class));
        button.setSize(SIZE, SIZE);
        button.setPosition(START_X, START_Y);
        button.toFront();
        addActor(button);
        button.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                new ConfirmationDialog().show(getStage());
                return true;
            }
        });
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void dispose() {
    }

    private class ConfirmationDialog extends Dialog {

        public ConfirmationDialog() {
            super("", BlackoutGame.get().assets().getDefaultSkin());
            setMovable(false);
            pad(DIALOG_PADDING);

            getContentTable().add("Are you sure that you want to exit?");

            final TextButton exitButton = new TextButton("Exit", BlackoutGame.get().assets().getDefaultSkin());
            exitButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ConfirmationDialog.this.remove();
                    final GameWorld gameWorld = ExitButton.this.context.gameWorld();
                    if (gameWorld instanceof ClientGameWorld) {
                        final ClientGameWorld clientGameWorld = (ClientGameWorld) gameWorld;
                        clientGameWorld.interruptClientNetworkThread();
                    } else {
                        BlackoutGame.get().screenManager().disposeScreen();
                    }
                }
            });
            getButtonTable().add(exitButton).pad(DIALOG_PADDING);

            final TextButton cancelButton = new TextButton("Cancel", BlackoutGame.get().assets().getDefaultSkin());
            cancelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ConfirmationDialog.this.remove();
                }
            });
            getButtonTable().add(cancelButton).pad(DIALOG_PADDING);
        }
    }
}
