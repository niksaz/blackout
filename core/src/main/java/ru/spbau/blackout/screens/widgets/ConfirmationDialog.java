package ru.spbau.blackout.screens.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.worlds.ClientGameWorld;
import ru.spbau.blackout.worlds.GameWorld;

import static ru.spbau.blackout.BlackoutGame.DIALOG_PADDING;

/**
 * Dialog which asks a user whether he/she wants to leave. The same responses may be handled differently in distinct
 * contexts.
 */
public class ConfirmationDialog extends Dialog {

    private static final String EXIT = "Exit";
    private static final String CANCEL = "Cancel";

    public ConfirmationDialog(GameContext context, String title) {
        super("", BlackoutGame.get().assets().getDefaultSkin());
        setMovable(false);
        pad(DIALOG_PADDING);

        getContentTable().add(title);

        final TextButton exitButton = new TextButton(EXIT, BlackoutGame.get().assets().getDefaultSkin());
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (context == null) {
                    Gdx.app.exit();
                } else {
                    ConfirmationDialog.this.remove();
                    final GameWorld gameWorld = context.gameWorld();
                    if (gameWorld instanceof ClientGameWorld) {
                        final ClientGameWorld clientGameWorld = (ClientGameWorld) gameWorld;
                        clientGameWorld.interruptClientNetworkThread();
                    } else {
                        BlackoutGame.get().screenManager().disposeScreen();
                    }
                }
            }
        });
        getButtonTable().add(exitButton).pad(DIALOG_PADDING);

        final TextButton cancelButton = new TextButton(CANCEL, BlackoutGame.get().assets().getDefaultSkin());
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ConfirmationDialog.this.remove();
            }
        });
        getButtonTable().add(cancelButton).pad(DIALOG_PADDING);
    }
}
