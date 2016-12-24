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

    private static final String CONNECTION_MESSAGE = "Connecting to the server...";
    private static final String BACK_TEXT = "Back";

    private final Table middleTable;
    private final Label status;
    private final MenuScreen screen;
    private final AndroidClient task;

    private MultiplayerTable(MenuScreen screen, int port) {
        this.screen = screen;

        middleTable = new Table();

        addBlackoutLabel(middleTable);
        status = new Label(CONNECTION_MESSAGE, BlackoutGame.get().assets().getDefaultSkin());
        middleTable.add(status).row();

        task = new AndroidClient(this, port);
    }

    public static Table getTable(final MenuScreen screen, int port) {
        final MultiplayerTable result = new MultiplayerTable(screen, port);

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