package ru.spbau.blackout.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.database.PlayerEntity;
import ru.spbau.blackout.entities.Character;

import static ru.spbau.blackout.screens.MenuScreen.addButton;

public class UpgradesTable {

    private static final String HEALTH_TEXT = "Increase initial health.";

    private static final String BACK_TEXT = "Back";

    public static Table getTable(final MenuScreen screen) {
        final Table middleTable = new Table();

        addButton(middleTable, HEALTH_TEXT, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });

        final PlayerEntity entity = BlackoutGame.get().getPlayerEntity();
        final Character.Definition characterDefinition = entity.deserializeCharacterDefinition();
        for (Ability ability : characterDefinition.abilities) {
            addButton(middleTable, ability.getClass().toString(), null);
        }

        addButton(middleTable, BACK_TEXT, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.changeMiddleTable(MainMenuTable.getTable(screen));
            }
        });

        middleTable.setFillParent(true);
        return middleTable;
    }
}