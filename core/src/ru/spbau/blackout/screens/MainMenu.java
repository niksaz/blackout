package ru.spbau.blackout.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import ru.spbau.blackout.BlackoutGame;

public class MainMenu extends MenuScreen {

    private static final Color MAIN_MENU_BLACKOUT_LABEL_COLOR = Color.WHITE;

    private static final float MAIN_MENU_BLACKOUT_LABEL_BOTTOM_PADDING = 25.0f;
    private static final float MAIN_MENU_BLACKOUT_LABEL_SCALE = 2.5f;

    private static final String BLACKOUT_TEXT = "Blackout";
    private static final String MAIN_MENU_BUTTON_PLAY_TEXT = "Play";
    private static final String MAIN_MENU_BUTTON_SHOP_TEXT = "Shop";
    private static final String MAIN_MENU_BUTTON_ACHIEVEMENTS_TEXT = "Achievements";
    private static final String MAIN_MENU_BUTTON_LEADERBOARD_TEXT = "Leaderboard";

    public MainMenu(BlackoutGame game) {
        super(game);

        final Table middleTable = new Table();
        addBlackoutLabel(middleTable);

        final Drawable upImage = new TextureRegionDrawable(new TextureRegion(new Texture(MENU_BUTTON_UP_TEXTURE_PATH)));
        final Drawable downImage = new TextureRegionDrawable(new TextureRegion(new Texture(MENU_BUTTON_DOWN_TEXTURE_PATH)));

        addButton(middleTable, MAIN_MENU_BUTTON_PLAY_TEXT, upImage, downImage, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                blackoutGame.setScreen(new PlayScreen(blackoutGame));
            }
        });
        addButton(middleTable, MAIN_MENU_BUTTON_SHOP_TEXT, upImage, downImage, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                blackoutGame.playServices.unlockAchievement(blackoutGame.playServices.getWin1vs1DuelId());
            }
        });
        addButton(middleTable, MAIN_MENU_BUTTON_ACHIEVEMENTS_TEXT, upImage, downImage, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                blackoutGame.playServices.showAchievements();
            }
        });
        addButton(middleTable, MAIN_MENU_BUTTON_LEADERBOARD_TEXT, upImage, downImage, null);

        middleTable.setFillParent(true);
        stage.addActor(middleTable);
    }

    private Label addBlackoutLabel(Table table) {
        final BitmapFont font = new BitmapFont();
        font.getData().scale(MAIN_MENU_BLACKOUT_LABEL_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        final LabelStyle style = new LabelStyle(font, MAIN_MENU_BLACKOUT_LABEL_COLOR);
        final Label label = new Label(BLACKOUT_TEXT, style);

        table.add(label).pad(MAIN_MENU_BLACKOUT_LABEL_BOTTOM_PADDING).row();

        return label;
    }

}
