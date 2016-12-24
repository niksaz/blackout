package ru.spbau.blackout.screens.tables;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.screens.MenuScreen;

import static ru.spbau.blackout.screens.MenuScreen.addBlackoutLabel;
import static ru.spbau.blackout.screens.MenuScreen.addButton;

public class SettingsTable {

    private static final String BACK_TEXT = "Back";

    private static final float SLIDER_KNOB_MIN_HEIGHT = 50.0f;
    private static final float SLIDER_KNOB_MIN_WIDTH = 20.0f;
    private static final float SLIDER_BACKGROUND_MIN_HEIGHT = 25.0f;

    private static final float SLIDER_MIN_VALUE = 0.0f;
    private static final float SLIDER_MAX_VALUE = 1.0f;
    private static final float SLIDER_STEP = 0.01f;

    public static Table getTable(final MenuScreen screen) {
        final Table middleTable = new Table();

        addBlackoutLabel(middleTable);

        final Slider slider =
                new Slider(SLIDER_MIN_VALUE, SLIDER_MAX_VALUE, SLIDER_STEP, false, BlackoutGame.get().assets().getDefaultSkin());
        slider.getStyle().knob.setMinHeight(SLIDER_KNOB_MIN_HEIGHT);
        slider.getStyle().knob.setMinWidth(SLIDER_KNOB_MIN_WIDTH);
        slider.getStyle().background.setMinHeight(SLIDER_BACKGROUND_MIN_HEIGHT);
        middleTable.add(slider).fill(true, false).row();

        addButton(middleTable, BACK_TEXT, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.changeMiddleTable(ru.spbau.blackout.screens.tables.MainMenuTable.getTable(screen));
            }
        });

        return middleTable;
    }
}
