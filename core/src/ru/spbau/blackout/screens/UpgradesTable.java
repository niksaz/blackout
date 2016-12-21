package ru.spbau.blackout.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.database.PlayerEntity;
import ru.spbau.blackout.entities.Character;

public class UpgradesTable {

    private static final float BUTTON_WIDTH = 300.0f;
    private static final float BUTTON_HEIGHT = 50.0f;
    private static final float BUTTON_PADDING = 10.0f;

    private static final String HEALTH_TEXT = "Increase initial health";
    private static final String UPGRADE = "Upgrade";

    private static final String BACK_TEXT = "Back";

    public static Table getTable(final MenuScreen screen) {
        final Table middleTable = new Table();

        final PlayerEntity entity = BlackoutGame.get().getPlayerEntity();
        final Character.Definition characterDefinition = entity.deserializeCharacterDefinition();

        addButton(middleTable, HEALTH_TEXT, "current health " + characterDefinition.maxHealth);

        for (Ability ability : characterDefinition.abilities) {
            addButton(middleTable,
                      UPGRADE + " " + ability.getClass().getSimpleName(),
                      "current level " + ability.getLevel());
        }

        final TextButton button = new TextButton(BACK_TEXT, BlackoutGame.get().assets().getDefaultSkin());
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.changeMiddleTable(MainMenuTable.getTable(screen));
            }
        });
        middleTable.add(button).colspan(2).fill(true, false).pad(BUTTON_PADDING).height(BUTTON_HEIGHT);

        middleTable.setFillParent(true);
        return middleTable;
    }

    private static void addButton(Table table, String buttonText, String labelText) {
        final TextButton button = new TextButton(buttonText, BlackoutGame.get().assets().getDefaultSkin());
        table.add(button).pad(BUTTON_PADDING).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        final Label currentLabel = new Label(labelText, BlackoutGame.get().assets().getDefaultSkin());
        table.add(currentLabel).row();
    }
}