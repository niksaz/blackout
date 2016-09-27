package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.utils.Constants;

public class MainMenu extends BlackoutScreen {

    private static final String BLACKOUT_TEXT = "Blackout";

    private static final Color MAIN_MENU_BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.1f, 1.0f);
    private static final Color MAIN_MENU_BLACKOUT_LABEL_COLOR = Color.WHITE;

    private static final float MAIN_MENU_BUTTON_PADDING = 10.f;
    private static final float MAIN_MENU_BUTTON_TEXT_SCALE = 1.5f;
    private static final float MAIN_MENU_BLACKOUT_LABEL_BOTTOM_PADDING = 25.0f;
    private static final float MAIN_MENU_BLACKOUT_LABEL_SCALE = 2.5f;
    private static final float MAIN_MENU_SETTINGS_BUTTON_SIZE = 128.0f;
    private static final float MAIN_MENU_SETTINGS_BUTTON_PADDING = 12.0f;

    private static final String MAIN_MENU_SETTINGS_TEXTURE_PATH = "images/mainmenu/settings.png";
    private static final String MAIN_MENU_BUTTON_TEXTURE_PATH = "images/mainmenu/button.png";
    private static final String MAIN_MENU_BUTTON_PLAY_TEXT = "Play";
    private static final String MAIN_MENU_BUTTON_SHOP_TEXT = "Shop";
    private static final String MAIN_MENU_BUTTON_LEADERBOARD_TEXT = "Leaderboard";

    private Stage stage;

    public MainMenu(BlackoutGame blackoutGame) {
        super(blackoutGame);

        stage = new Stage(new ExtendViewport(Constants.VIRTUAL_WORLD_WIDTH, Constants.VIRTUAL_WORLD_HEIGHT));

        Table middleTable = new Table();
        addBlackoutLabel(middleTable);

        Drawable buttonImage =
                new TextureRegionDrawable(new TextureRegion(new Texture(MAIN_MENU_BUTTON_TEXTURE_PATH)));
        addButton(middleTable, MAIN_MENU_BUTTON_PLAY_TEXT, buttonImage);
        addButton(middleTable, MAIN_MENU_BUTTON_SHOP_TEXT, buttonImage);
        addButton(middleTable, MAIN_MENU_BUTTON_LEADERBOARD_TEXT, buttonImage);

        middleTable.setFillParent(true);
        stage.addActor(middleTable);

        addSettingsButton();
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

    private void addBlackoutLabel(Table table) {
        BitmapFont font = new BitmapFont();
        font.getData().scale(MAIN_MENU_BLACKOUT_LABEL_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Label.LabelStyle style = new Label.LabelStyle(font, MAIN_MENU_BLACKOUT_LABEL_COLOR);
        Label label = new Label(BLACKOUT_TEXT, style);

        table.add(label).pad(MAIN_MENU_BLACKOUT_LABEL_BOTTOM_PADDING).row();
    }

    private void addButton(Table table, String text, Drawable image) {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(MAIN_MENU_BUTTON_TEXT_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        style.up = image;
        style.down = image;

        TextButton button = new TextButton(text, style);

        table.add(button).pad(MAIN_MENU_BUTTON_PADDING).row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(
                MAIN_MENU_BACKGROUND_COLOR.r,
                MAIN_MENU_BACKGROUND_COLOR.g,
                MAIN_MENU_BACKGROUND_COLOR.b,
                MAIN_MENU_BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
