package ru.spbau.blackout.screens.tables;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.screens.MenuScreen;

import static ru.spbau.blackout.screens.MenuScreen.addBlackoutLabel;
import static ru.spbau.blackout.screens.MenuScreen.addButton;

/**
 * Middle table after a player clicks play button on the first table.
 */
public class PlayScreenTable  {

    private static final String SINGLE_PLAYER_GAME_TEXT = "Single player game";
    private static final String MULTIPLAYER_GAME_TEXT = "Multiplayer game";

    public static Table getTable(final MenuScreen screen) {
        final Table middleTable = new Table();

        addBlackoutLabel(middleTable);
        addButton(middleTable, SINGLE_PLAYER_GAME_TEXT, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BlackoutGame.get().startTestSinglePlayerGame();
            }
        });
        addButton(middleTable, MULTIPLAYER_GAME_TEXT, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.changeMiddleTable(MultiplayerTable.getTable(screen));
            }
        });
        screen.addBackToMainMenuButton(middleTable);

        return middleTable;
    }

}
