package ru.spbau.blackout.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static ru.spbau.blackout.screens.MenuScreen.addBlackoutLabel;
import static ru.spbau.blackout.screens.MenuScreen.addButton;

/**
 * Middle table which a player faces every time he completes signing-in.
 */
class MainMenuTable {

    private static final String BUTTON_PLAY_TEXT = "Play";
    private static final String BUTTON_UPGRADES_TEXT = "Upgrades";
    private static final String BUTTON_SETTINGS = "Settings";

    static Table getTable(final MenuScreen screen) {
        final Table middleTable = new Table();

        addBlackoutLabel(middleTable);

        addButton(middleTable, BUTTON_PLAY_TEXT, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.changeMiddleTable(PlayScreenTable.getTable(screen));
            }
        });
        addButton(middleTable, BUTTON_UPGRADES_TEXT, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screen.changeMiddleTable(UpgradesTable.getTable(screen));
            }
        });
        addButton(middleTable, BUTTON_SETTINGS, null);

        middleTable.setFillParent(true);
        return middleTable;
    }
}
