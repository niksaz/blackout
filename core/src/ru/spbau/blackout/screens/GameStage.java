package ru.spbau.blackout.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.worlds.ClientGameWorld;
import ru.spbau.blackout.worlds.GameWorld;

import static com.badlogic.gdx.Input.Keys;
import static ru.spbau.blackout.BlackoutGame.DIALOG_PADDING;

/**
 * Stage which also responds to back button by showing a dialog.
 */
public class GameStage extends Stage {

    private final GameContext context;

    public GameStage(Viewport viewport, SpriteBatch spriteBatch, GameContext context) {
        super(viewport, spriteBatch);
        this.context = context;
    }

    @Override
    public boolean keyDown(int keyCode) {
        if (keyCode == Keys.BACK) {
            new ConfirmationDialog(context).show(this);
            return true;
        }
        return super.keyDown(keyCode);
    }

    /**
     * Dialog which asks a user whether he/she wants to leave.
     */
    private static class ConfirmationDialog extends Dialog {

        public ConfirmationDialog(GameContext context) {
            super("", BlackoutGame.get().assets().getDefaultSkin());
            setMovable(false);
            pad(DIALOG_PADDING);

            getContentTable().add("Are you sure that you want to leave the battle?");

            final TextButton exitButton = new TextButton("Exit", BlackoutGame.get().assets().getDefaultSkin());
            exitButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    ConfirmationDialog.this.remove();
                    final GameWorld gameWorld = context.gameWorld();
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
                    GameStage.ConfirmationDialog.this.remove();
                }
            });
            getButtonTable().add(cancelButton).pad(DIALOG_PADDING);
        }
    }
}
