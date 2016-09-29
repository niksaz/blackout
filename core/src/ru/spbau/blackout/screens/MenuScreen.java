package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import ru.spbau.blackout.BlackoutGame;

abstract class MenuScreen extends BlackoutScreen {

    protected static final Color  MENU_BACKGROUND_COLOR = new Color(0.1f, 0.1f, 0.1f, 1.0f);
    protected static final String MENU_BUTTON_UP_TEXTURE_PATH = "images/menuscreen/button_up.png";
    protected static final String MENU_BUTTON_DOWN_TEXTURE_PATH = "images/menuscreen/button_down.png";
    protected static final String MENU_SETTINGS_TEXTURE_PATH = "images/menuscreen/settings.png";

    protected static final float  MENU_BUTTON_TEXT_SCALE = 1.5f;
    protected static final float  MENU_BUTTON_PADDING = 10.0f;
    protected static final float  PLAYER_LABEL_MARGIN_X = 20.0f;
    protected static final float  PLAYER_LABEL_MARGIN_Y = 30.0f;
    protected static final float  MENU_SETTINGS_BUTTON_SIZE = 128.0f;
    protected static final float  MENU_SETTINGS_BUTTON_PADDING = 12.0f;

    protected Stage stage;
    protected Label playerNameLabel;

    MenuScreen(BlackoutGame blackoutGame) {
        super(blackoutGame);

        stage = new Stage(new ExtendViewport(BlackoutGame.VIRTUAL_WORLD_WIDTH, BlackoutGame.VIRTUAL_WORLD_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        playerNameLabel = addPlayerNameLabel();
        addSettingsImage();
    }

    protected TextButton addButton(Table table, String text, Drawable upImage, Drawable downImage, EventListener listener) {
        final BitmapFont font = new BitmapFont();
        font.getData().setScale(MENU_BUTTON_TEXT_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        final TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.up = upImage;
        style.down = downImage;

        final TextButton button = new TextButton(text, style);
        if (listener != null) {
            button.addListener(listener);
        }

        table.add(button).pad(MENU_BUTTON_PADDING).row();

        return button;
    }

    private Label addPlayerNameLabel() {
        final Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont();

        final Label label = new Label("", style);

        label.setX(PLAYER_LABEL_MARGIN_X);
        label.setY(BlackoutGame.VIRTUAL_WORLD_HEIGHT - PLAYER_LABEL_MARGIN_Y);
        stage.addActor(label);

        return label;
    }

    private Image addSettingsImage() {
        final Texture settingsTexture = new Texture(MENU_SETTINGS_TEXTURE_PATH);
        Image settingsImage = new Image(settingsTexture);

        final Container<Image> space = new Container<Image>(settingsImage);
        space.setWidth(MENU_SETTINGS_BUTTON_SIZE);
        space.setHeight(MENU_SETTINGS_BUTTON_SIZE);
        space.pad(MENU_SETTINGS_BUTTON_PADDING);

        stage.addActor(space);

        return settingsImage;
    }

    private void refreshPlayerName() {
        final String playerName;
        if (blackoutGame.playServices.isSignedIn()) {
            playerName = blackoutGame.playServices.getPlayerName();
        } else {
            playerName = "unknown";
        }
        playerNameLabel.setText("Hello, " + playerName + "!");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(
                MENU_BACKGROUND_COLOR.r, MENU_BACKGROUND_COLOR.g,
                MENU_BACKGROUND_COLOR.b, MENU_BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        refreshPlayerName();
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }

}
