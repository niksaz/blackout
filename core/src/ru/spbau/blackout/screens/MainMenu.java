package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

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
    private static final String MAIN_MENU_BUTTON_LEADERBOARD_TEXT = "Leaderboard";

    private Stage stage;

    public MainMenu(BlackoutGame game) {
        super(game);

        stage = new Stage(new ExtendViewport(BlackoutGame.VIRTUAL_WORLD_WIDTH,
                                                BlackoutGame.VIRTUAL_WORLD_HEIGHT));
        Gdx.input.setInputProcessor(stage);

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

        addButton(middleTable, MAIN_MENU_BUTTON_LEADERBOARD_TEXT, upImage, downImage, null);

        middleTable.setFillParent(true);
        stage.addActor(middleTable);

        addSettingsButton();

        addPlayerName();
    }

    private Label addPlayerName() {
        LabelStyle style = new LabelStyle();
        style.font = new BitmapFont();

        String playerName = blackoutGame.playServices.getPlayerName();
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

    private TextButton addButton(Table table, String text, Drawable image, EventListener listener) {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(MenuScreen.MENU_BUTTON_TEXT_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        style.up = image;
        style.down = image;

        TextButton button = new TextButton(text, style);
        if (listener != null) {
            button.addListener(listener);
        }

        table.add(button).pad(MAIN_MENU_BUTTON_PADDING).row();

        return button;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(
                MenuScreen.MENU_BACKGROUND_COLOR.r, MenuScreen.MENU_BACKGROUND_COLOR.g,
                MenuScreen.MENU_BACKGROUND_COLOR.b, MenuScreen.MENU_BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
