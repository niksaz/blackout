package ru.spbau.blackout.screens.tables;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.network.AndroidClient;
import ru.spbau.blackout.screens.MenuScreen;

import static ru.spbau.blackout.screens.MenuScreen.addBlackoutLabel;
import static ru.spbau.blackout.screens.MenuScreen.addButton;

/**
 * Middle table which a player sees during multi-player match search.
 */
public class MultiplayerTable {

    private static final String BACK_TEXT = "Back";

    private final Table middleTable;
    private final Label status;
    private final MenuScreen screen;
    private final AndroidClient task;

    private MultiplayerTable(MenuScreen screen) {
        this.screen = screen;

        middleTable = new Table();

        addBlackoutLabel(middleTable);
        final Label.LabelStyle style = new Label.LabelStyle();
        style.font = BlackoutGame.get().assets().getFont();
        status = new Label("Connecting to the server...", style);
        middleTable.add(status).row();

        task = new AndroidClient(this);
    }

    public static Table getTable(final MenuScreen screen) {
        final MultiplayerTable result = new MultiplayerTable(screen);

        addButton(result.middleTable, BACK_TEXT, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                result.task.stop();
            }
        });

        new Thread(result.task).start();
        return result.middleTable;
    }

    public Label getStatusLabel() {
        return status;
    }

    public MenuScreen getMenuScreen() {
        return screen;
    }
}