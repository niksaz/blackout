package ru.spbau.blackout.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ru.spbau.blackout.BlackoutGame;

import static ru.spbau.blackout.screens.MenuScreen.addBlackoutLabel;
import static ru.spbau.blackout.screens.MenuScreen.addButton;

/**
 * Middle table which a player faces every time he completes signing-in.
 */
class MainMenuTable {

    private static final String BUTTON_PLAY_TEXT = "Play";
    private static final String BUTTON_SHOP_TEXT = "Try your luck!";
    private static final String BUTTON_UPGRADES_TEXT = "Upgrades";
    private static final String BUTTON_ACHIEVEMENTS_TEXT = "Achievements";
    private static final String BUTTON_LEADERBOARD_TEXT = "Leaderboard";


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
        addButton(middleTable, BUTTON_ACHIEVEMENTS_TEXT, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BlackoutGame.get().playServicesInCore().getPlayServices().showAchievements();
            }
        });
        addButton(middleTable, BUTTON_LEADERBOARD_TEXT, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BlackoutGame.get().playServicesInCore().getPlayServices().showLeaderboards();
            }
        });

        middleTable.setFillParent(true);
        return middleTable;
    }

}
