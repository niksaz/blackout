package ru.spbau.blackout.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.screens.widgets.ConfirmationDialog;

import static com.badlogic.gdx.Input.Keys;

/**
 * In-game stage which also responds to back button by showing a dialog.
 */
public class GameStage extends Stage {

    private static final String CONFIRMATION_MESSAGE = "Are you sure that you want to leave the battle?";

    private final GameContext context;

    public GameStage(Viewport viewport, SpriteBatch spriteBatch, GameContext context) {
        super(viewport, spriteBatch);
        this.context = context;
    }

    @Override
    public boolean keyDown(int keyCode) {
        if (keyCode == Keys.BACK) {
            new ConfirmationDialog(context, CONFIRMATION_MESSAGE).show(this);
            return true;
        }
        return super.keyDown(keyCode);
    }
}
