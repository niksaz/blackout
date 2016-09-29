package ru.spbau.blackout.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import ru.spbau.blackout.BlackoutGame;

public class MainMenu extends MenuScreen {

    private static final String BLACKOUT_TEXT = "Blackout";

    private static final Color MAIN_MENU_BLACKOUT_LABEL_COLOR = Color.WHITE;

    private static final float MAIN_MENU_BLACKOUT_LABEL_BOTTOM_PADDING = 25.0f;
    private static final float MAIN_MENU_BLACKOUT_LABEL_SCALE = 2.5f;
    private static final float MAIN_MENU_SETTINGS_BUTTON_SIZE = 128.0f;
    private static final float MAIN_MENU_SETTINGS_BUTTON_PADDING = 12.0f;
    private static final float PLAYER_LABEL_MARGIN_X = 20.0f;
    private static final float PLAYER_LABEL_MARGIN_Y = 30.0f;

    private static final String MAIN_MENU_SETTINGS_TEXTURE_PATH = "images/menuscreen/settings.png";
    private static final String MAIN_MENU_BUTTON_PLAY_TEXT = "Play";
    private static final String MAIN_MENU_BUTTON_SHOP_TEXT = "Shop";
    private static final String MAIN_MENU_BUTTON_ACHIEVEMENTS_TEXT = "Achievements";
    private static final String MAIN_MENU_BUTTON_LEADERBOARD_TEXT = "Leaderboard";

    public MainMenu(BlackoutGame game) {
        super(game);

        Table middleTable = new Table();
        addBlackoutLabel(middleTable);

        Drawable upImage =
                new TextureRegionDrawable(new TextureRegion(new Texture(MenuScreen.MENU_BUTTON_UP_TEXTURE_PATH)));
        Drawable downImage =
                new TextureRegionDrawable(new TextureRegion(new Texture(MenuScreen.MENU_BUTTON_DOWN_TEXTURE_PATH)));

        addButton(middleTable, MAIN_MENU_BUTTON_PLAY_TEXT, upImage, downImage, new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                blackoutGame.setScreen(new PlayScreen(blackoutGame));
            }
        });
        addButton(middleTable, MAIN_MENU_BUTTON_SHOP_TEXT, upImage, downImage, null);
        addButton(middleTable, MAIN_MENU_BUTTON_ACHIEVEMENTS_TEXT, upImage, downImage, null);
        addButton(middleTable, MAIN_MENU_BUTTON_LEADERBOARD_TEXT, upImage, downImage, null);

        middleTable.setFillParent(true);
        stage.addActor(middleTable);

        addSettingsButton();

        addPlayerName();
    }

    private Label addPlayerName() {
        LabelStyle style = new LabelStyle();
        style.font = new BitmapFont();

        final String playerName;
        if (blackoutGame.playServices.isSignedIn()) {
            playerName = blackoutGame.playServices.getPlayerName();
        } else {
            playerName = "unknown";
        }
        Label label = new Label("Hello, " + playerName + "!", style);

        label.setX(PLAYER_LABEL_MARGIN_X);
        label.setY(BlackoutGame.VIRTUAL_WORLD_HEIGHT - PLAYER_LABEL_MARGIN_Y);
        stage.addActor(label);

        return label;
    }

    private void addSettingsButton() {
        Texture settingsTexture = new Texture(MAIN_MENU_SETTINGS_TEXTURE_PATH);
        Image settingsImage = new Image(settingsTexture);

        Container<Image> space = new Container<Image>(settingsImage);
        space.setWidth(MAIN_MENU_SETTINGS_BUTTON_SIZE);
        space.setHeight(MAIN_MENU_SETTINGS_BUTTON_SIZE);
        space.pad(MAIN_MENU_SETTINGS_BUTTON_PADDING);

        stage.addActor(space);
    }

    private Label addBlackoutLabel(Table table) {
        BitmapFont font = new BitmapFont();
        font.getData().scale(MAIN_MENU_BLACKOUT_LABEL_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        LabelStyle style = new LabelStyle(font, MAIN_MENU_BLACKOUT_LABEL_COLOR);
        Label label = new Label(BLACKOUT_TEXT, style);

        table.add(label).pad(MAIN_MENU_BLACKOUT_LABEL_BOTTOM_PADDING).row();

        return label;
    }

}
