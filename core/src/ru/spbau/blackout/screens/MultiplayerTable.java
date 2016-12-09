package ru.spbau.blackout.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.network.AndroidClient;
import ru.spbau.blackout.utils.BlackoutAssets;

import static ru.spbau.blackout.screens.MenuScreen.addButton;

public class MultiplayerTable {

    private static final String BACK_TEXT = "Back";

    private final Table middleTable;
    private final Label status;
    private final MenuScreen screen;
    private final AndroidClient task;

    private MultiplayerTable(MenuScreen screen) {
        this.screen = screen;

        middleTable = new Table();
        final Label.LabelStyle style = new Label.LabelStyle();
        style.font = BlackoutGame.get().assets().getFont();
        status = new Label("Connecting to the server...", style);
        middleTable.add(status).row();

        task = new AndroidClient(this);
    }

    static Table getTable(final MenuScreen screen) {
        final MultiplayerTable result = new MultiplayerTable(screen);

        final Drawable upImage = new TextureRegionDrawable(
                new TextureRegion(new Texture(MenuScreen.BUTTON_UP_TEXTURE_PATH)));
        final Drawable downImage = new TextureRegionDrawable(
                new TextureRegion(new Texture(MenuScreen.BUTTON_DOWN_TEXTURE_PATH)));

        addButton(result.middleTable, BACK_TEXT, upImage, downImage, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                result.task.stop();
            }
        });

        new Thread(result.task).start();
        result.middleTable.setFillParent(true);
        return result.middleTable;
    }

    public Label getStatusLabel() {
        return status;
    }

    public MenuScreen getMenuScreen() {
        return screen;
    }
}
