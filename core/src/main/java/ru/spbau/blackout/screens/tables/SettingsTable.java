package ru.spbau.blackout.screens.tables;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.database.ChangeablePlayerProfile;
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
    private static final String MUSIC_LABEL = "Music";
    private static final String SOUND_LABEL = "Sound";

    public static Table getTable(final MenuScreen screen) {
        final Table middleTable = new Table();

        addBlackoutLabel(middleTable, 2);

        final ChangeablePlayerProfile entity = BlackoutGame.get().getPlayerEntity();
        final GameSettings gameSettings = entity.getGameSettings();

        addRowWithLabelAndSlider(
                middleTable, MUSIC_LABEL, gameSettings.musicVolume,
                currentValue -> {
                    gameSettings.musicVolume = currentValue;
                    entity.setGameSettings(gameSettings);
                    screen.updateMusicVolume();
                }
        );
        addRowWithLabelAndSlider(
                middleTable, SOUND_LABEL, gameSettings.soundVolume,
                currentValue -> {
                    gameSettings.soundVolume = currentValue;
                    entity.setGameSettings(gameSettings);
                }
        );
        screen.addBackToMainMenuButton(middleTable, 2);

        return middleTable;
    }

    private static void addRowWithLabelAndSlider(Table table, String labelText, float value, StateUpdater updater) {
        final Label label = new Label(labelText, BlackoutGame.get().assets().getDefaultSkin());
        table.add(label).width(LABEL_WIDTH);
        final Slider slider =
                new Slider(SLIDER_MIN_VALUE, SLIDER_MAX_VALUE, SLIDER_STEP, false, BlackoutGame.get().assets().getDefaultSkin());
        slider.getStyle().knob.setMinHeight(SLIDER_KNOB_MIN_HEIGHT);
        slider.getStyle().knob.setMinWidth(SLIDER_KNOB_MIN_WIDTH);
        slider.getStyle().background.setMinHeight(SLIDER_BACKGROUND_MIN_HEIGHT);
        slider.setValue(value);
        slider.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                updater.update(slider.getValue());
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                updater.update(slider.getValue());
                super.touchDragged(event, x, y, pointer);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                updater.update(slider.getValue());
                BlackoutGame.get().getPlayerEntity().synchronizeGameSettings();
                super.touchUp(event, x, y, pointer, button);
            }
        });
        table.add(slider).pad(SLIDER_PADDING).width(SLIDER_BACKGROUND_MIN_WIDTH).fill(true, false).row();
    }

    private interface StateUpdater {
        void update(float currentValue);
    }
}
