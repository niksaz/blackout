package ru.spbau.blackout.screens.tables;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ru.spbau.blackout.screens.MenuScreen;

import static ru.spbau.blackout.screens.MenuScreen.*;
import static ru.spbau.blackout.screens.MenuScreen.addButton;

/**
 * Middle table which a player faces every time he completes signing-in.
 */
public class MainMenuTable {

    private static final String BUTTON_PLAY_TEXT = "Play";
    private static final String BUTTON_UPGRADES_TEXT = "Upgrades";
    private static final String BUTTON_SETTINGS = "Settings";

    public static Table getTable(final MenuScreen screen) {
        final Table middleTable = new Table();

        addBlackoutLabel(middleTable);
        addButton(middleTable, BUTTON_PLAY_TEXT, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.changeMiddleTable(PlayScreenTable.getTable(screen));
                super.clicked(event, x, y);
            }
        });
        addButton(middleTable, BUTTON_UPGRADES_TEXT, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.changeMiddleTable(UpgradesTable.getTable(screen));
                super.clicked(event, x, y);
            }
        });
        addButton(middleTable, BUTTON_SETTINGS, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.changeMiddleTable(SettingsTable.getTable(screen));
                super.clicked(event, x, y);
            }
        });

        return middleTable;
    }
}
