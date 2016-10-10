package ru.spbau.blackout.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import ru.spbau.blackout.BlackoutGame;

import static ru.spbau.blackout.screens.MenuScreen.addButton;

public class MainMenuTable {

    private static final Color BLACKOUT_LABEL_COLOR = Color.WHITE;

    private static final float BLACKOUT_LABEL_BOTTOM_PADDING = 25.0f;
    private static final float BLACKOUT_LABEL_SCALE = 2.5f;

    private static final String BLACKOUT_TEXT = "Blackout";
    private static final String BUTTON_PLAY_TEXT = "Play";
    private static final String BUTTON_SHOP_TEXT = "* Add 10 gold";
    private static final String BUTTON_ACHIEVEMENTS_TEXT = "Achievements";
    private static final String BUTTON_LEADERBOARD_TEXT = "Leaderboard";

    public static Table getTable(final BlackoutGame game, final MenuScreen screen) {
        final Table middleTable = new Table();
        addBlackoutLabel(middleTable);

        final Drawable upImage = new TextureRegionDrawable(new TextureRegion(new Texture(MenuScreen.BUTTON_UP_TEXTURE_PATH)));
        final Drawable downImage = new TextureRegionDrawable(new TextureRegion(new Texture(MenuScreen.BUTTON_DOWN_TEXTURE_PATH)));

        addButton(middleTable, BUTTON_PLAY_TEXT, upImage, downImage, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screen.changeMiddleTable(PlayScreenTable.getTable(game, screen));
            }
        });
        addButton(middleTable, BUTTON_SHOP_TEXT, upImage, downImage, new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getSnapshot().changeGold(10);
            }
        });
        addButton(middleTable, BUTTON_ACHIEVEMENTS_TEXT, upImage, downImage, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.playServices.showAchievements();
            }
        });
        addButton(middleTable, BUTTON_LEADERBOARD_TEXT, upImage, downImage, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.playServices.showLeaderboards();
            }
        });

        middleTable.setFillParent(true);
        return middleTable;
    }

    private static Label addBlackoutLabel(Table table) {
        final BitmapFont font = new BitmapFont();
        font.getData().scale(BLACKOUT_LABEL_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        final LabelStyle style = new LabelStyle(font, BLACKOUT_LABEL_COLOR);
        final Label label = new Label(BLACKOUT_TEXT, style);

        table.add(label).pad(BLACKOUT_LABEL_BOTTOM_PADDING).row();

        return label;
    }

}
