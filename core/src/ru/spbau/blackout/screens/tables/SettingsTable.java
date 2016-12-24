package ru.spbau.blackout.screens.tables;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.database.PlayerProfile;
import ru.spbau.blackout.screens.MenuScreen;
import ru.spbau.blackout.settings.GameSettings;

import static ru.spbau.blackout.screens.MenuScreen.addBlackoutLabel;

public class SettingsTable {

    private static final float SLIDER_KNOB_MIN_WIDTH = 20.0f;
    private static final float SLIDER_KNOB_MIN_HEIGHT = 50.0f;
    private static final float SLIDER_BACKGROUND_MIN_WIDTH = 500.0f;
    private static final float SLIDER_BACKGROUND_MIN_HEIGHT = 25.0f;

    private static final float SLIDER_MIN_VALUE = 0.0f;
    private static final float SLIDER_MAX_VALUE = 1.0f;
    private static final float SLIDER_STEP = 0.01f;
    private static final float SLIDER_PADDING = 10.0f;

    private static final float LABEL_WIDTH = 150.0f;

    public static Table getTable(final MenuScreen screen) {
        final Table middleTable = new Table();

        addBlackoutLabel(middleTable, 2);

        final PlayerProfile entity = BlackoutGame.get().getPlayerEntity();
        final GameSettings gameSettings = entity.getGameSettings();

        addRowWithLabelAndSlider("Music", gameSettings.musicVolume, middleTable);
        addRowWithLabelAndSlider("Sound", gameSettings.soundVolume, middleTable);
        screen.addBackToMainMenuButton(middleTable, 2);

        return middleTable;
    }

    private static void addRowWithLabelAndSlider(String labelText, float value, Table table) {
        final Label label = new Label(labelText, BlackoutGame.get().assets().getDefaultSkin());
        table.add(label).width(LABEL_WIDTH);
        final Slider slider =
                new Slider(SLIDER_MIN_VALUE, SLIDER_MAX_VALUE, SLIDER_STEP, false, BlackoutGame.get().assets().getDefaultSkin());
        slider.getStyle().knob.setMinHeight(SLIDER_KNOB_MIN_HEIGHT);
        slider.getStyle().knob.setMinWidth(SLIDER_KNOB_MIN_WIDTH);
        slider.getStyle().background.setMinHeight(SLIDER_BACKGROUND_MIN_HEIGHT);
        slider.setValue(value);
        table.add(slider).pad(SLIDER_PADDING).width(SLIDER_BACKGROUND_MIN_WIDTH).fill(true, false).row();
    }
}
