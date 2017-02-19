package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import ru.spbau.blackout.BlackoutGame;

import static com.badlogic.gdx.Input.Keys;
import static ru.spbau.blackout.BlackoutGame.DIALOG_PADDING;

/**
 * Stage which also responds to back button by showing a dialog.
 */
public class MenuStage extends Stage {

    public MenuStage(ExtendViewport extendViewport) {
        super(extendViewport);
    }

    @Override
    public boolean keyDown(int keyCode) {
        if (keyCode == Keys.BACK) {
            new ConfirmationDialog().show(this);
            return true;
        }
        return super.keyDown(keyCode);
    }

    /**
     * Dialog which asks a user whether he/she wants to leave.
     */
    private static class ConfirmationDialog extends Dialog {

        public ConfirmationDialog() {
            super("", BlackoutGame.get().assets().getDefaultSkin());
            setMovable(false);
            pad(DIALOG_PADDING);

            getContentTable().add("Do you want to exit the game?");

            final TextButton exitButton = new TextButton("Exit", BlackoutGame.get().assets().getDefaultSkin());
            exitButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.exit();
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
