package ru.spbau.blackout.screens.tables;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.abilities.Ability;
import ru.spbau.blackout.database.PlayerProfile;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.screens.MenuScreen;

import static ru.spbau.blackout.database.Database.HEALTH_UPGRADE_COST;
import static ru.spbau.blackout.screens.MenuScreen.BUTTON_HEIGHT;
import static ru.spbau.blackout.screens.MenuScreen.BUTTON_PADDING;
import static ru.spbau.blackout.screens.MenuScreen.addBlackoutLabel;

public class UpgradesTable {

    private static final String HEALTH_UPGRADE = "Increase health";
    private static final String HEALTH_CURRENT = "max health";
    private static final String ABILITY_UPGRADE = "Upgrade";
    private static final String ABILITY_CURRENT = "level";

    public static Table getTable(final MenuScreen screen) {
        final Table middleTable = new Table();
        addBlackoutLabel(middleTable, 2);

        addRowWithButtonAndLabel(
                middleTable,
                new TextButton(
                        appendPrice(HEALTH_UPGRADE, HEALTH_UPGRADE_COST),
                        BlackoutGame.get().assets().getDefaultSkin()),
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        BlackoutGame.get().getPlayerEntity().upgradeHealth();
                        super.clicked(event, x, y);
                    }
                },
                new Label("", BlackoutGame.get().assets().getDefaultSkin()) {
                    @Override
                    public void act(float delta) {
                        setText(HEALTH_CURRENT + " " +
                                BlackoutGame.get().getPlayerEntity().getCharacterDefinition().maxHealth);
                        super.act(delta);
                    }
                });

        final PlayerProfile entity = BlackoutGame.get().getPlayerEntity();
        final Character.Definition characterDefinition = entity.getCharacterDefinition();
        for (int abilityIndex = 0; abilityIndex < characterDefinition.abilities.length; abilityIndex++) {
            final int currentAbilityIndex = abilityIndex;
            addRowWithButtonAndLabel(
                    middleTable,
                    new TextButton("", BlackoutGame.get().assets().getDefaultSkin()) {
                        @Override
                        public void act(float delta) {
                            final Ability.Definition ability =
                                    BlackoutGame.get().getPlayerEntity().getCharacterDefinition()
                                    .abilities[currentAbilityIndex];
                            setText(appendPrice(ABILITY_UPGRADE + " " + ability.name(), ability.getUpgradeCost()));
                            super.act(delta);
                        }
                    },
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            BlackoutGame.get().getPlayerEntity().upgradeAbility(currentAbilityIndex);
                            super.clicked(event, x, y);
                        }
                    },
                    new Label("", BlackoutGame.get().assets().getDefaultSkin()) {
                        @Override
                        public void act(float delta) {
                            setText(ABILITY_CURRENT + " " +
                                    BlackoutGame.get().getPlayerEntity().getCharacterDefinition()
                                            .abilities[currentAbilityIndex].getLevel());
                            super.act(delta);
                        }
                    });
        }
        screen.addBackToMainMenuButton(middleTable, 2);

        return middleTable;
    }

    private static void addRowWithButtonAndLabel(Table table, Button button,
                                                 EventListener buttonListener, Label labelAfter) {
        button.addListener(buttonListener);
        table.add(button).fill(true, false).pad(BUTTON_PADDING).height(BUTTON_HEIGHT);
        table.add(labelAfter).row();
    }

    private static String appendPrice(String statement, int price) {
        return statement + " for " + price + " gold";
    }
}
