package ru.spbau.blackout.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import ru.spbau.blackout.screens.widgets.ConfirmationDialog;

import static com.badlogic.gdx.Input.Keys;

/**
 * Menu stage which also responds to back button by showing a dialog.
 */
public class MenuStage extends Stage {

    private static final String CONFIRMATION_MESSAGE = "Do you want to exit the game?";

    public MenuStage(ExtendViewport extendViewport) {
        super(extendViewport);
    }

    @Override
    public boolean keyDown(int keyCode) {
        if (keyCode == Keys.BACK) {
            new ConfirmationDialog(null, CONFIRMATION_MESSAGE).show(this);
            return true;
        }
        return super.keyDown(keyCode);
    }
}
