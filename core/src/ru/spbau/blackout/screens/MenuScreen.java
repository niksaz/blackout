package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.play.services.PlayServicesInCore;
import ru.spbau.blackout.utils.AssetLoader;
import ru.spbau.blackout.utils.ScreenManager;

class MenuScreen extends StageScreen {

    private static final Color BACKGROUND_COLOR = new Color(0.2f, 0.2f, 0.2f, 1.0f);
    static final String BUTTON_UP_TEXTURE_PATH = "images/menuscreen/button_up.png";
    static final String BUTTON_DOWN_TEXTURE_PATH = "images/menuscreen/button_down.png";
    private static final String SETTINGS_TEXTURE_PATH = "images/menuscreen/settings.png";
    private static final String GAME_SERVICES_TEXTURE_PATH = "images/menuscreen/games_controller_grey.png";

    private static final float BUTTON_PADDING = 10.0f;
    private static final float CORNER_LABEL_MARGIN = 20.0f;
    private static final float SETTINGS_ICON_SIZE = 128.0f;
    private static final float SETTINGS_ICON_PADDING = 12.0f;

    private Label goldLabel;
    private Table middleTable;

    MenuScreen(BlackoutGame blackoutGame) {
        super(blackoutGame);
        addLeftPaneElements();
        addRightPaneElements();
        changeMiddleTable(MainMenuTable.getTable(blackoutGame, this));
    }

    void changeMiddleTable(Table table) {
        if (middleTable != null) {
            middleTable.remove();
        }
        middleTable = table;
        stage.addActor(middleTable);
    }

    private void addLeftPaneElements() {
        addLabelWithTextAt(
                "Hello, " + PlayServicesInCore.getInstance().getPlayServices().getPlayerName(),
                CORNER_LABEL_MARGIN,
                stage.getViewport().getWorldHeight() - CORNER_LABEL_MARGIN,
                Align.topLeft);
        final Actor playServicesIcon = addGooglePlayGamesServicesIcon();
        addSettingsIcon(playServicesIcon);
    }

    private void addRightPaneElements() {
        goldLabel = addLabelWithTextAt("", 0, 0, 0);
    }

    static TextButton addButton(Table table, String text, Drawable upImage, Drawable downImage, EventListener listener) {
        final TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = AssetLoader.getInstance().getFont();
        style.up = upImage;
        style.down = downImage;

        final TextButton button = new TextButton(text, style);
        if (listener != null) {
            button.addListener(listener);
        }

        table.add(button).pad(BUTTON_PADDING).row();

        return button;
    }

    private Label addLabelWithTextAt(CharSequence text, float x, float y, int align) {
        final Label.LabelStyle style = new Label.LabelStyle();
        style.font = AssetLoader.getInstance().getFont();

        final Label label = new Label(text, style);
        label.setPosition(x, y, align);
        stage.addActor(label);

        return label;
    }

    private Image addSettingsIcon(final Actor controlledImage) {
        final Texture settingsTexture = new Texture(SETTINGS_TEXTURE_PATH);
        final Image settingsImage = new Image(settingsTexture);

        settingsImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controlledImage.setVisible(!controlledImage.isVisible());
            }
        });

        final Container<Image> settingsContainer = new Container<Image>(settingsImage);
        settingsContainer.setWidth(SETTINGS_ICON_SIZE);
        settingsContainer.setHeight(SETTINGS_ICON_SIZE);
        settingsContainer.pad(SETTINGS_ICON_PADDING);
        stage.addActor(settingsContainer);

        return settingsImage;
    }

    private Image addGooglePlayGamesServicesIcon() {
        final Texture gamesServices = new Texture(GAME_SERVICES_TEXTURE_PATH);
        Image gamesServicesImage = new Image(gamesServices);
        gamesServicesImage.setVisible(false);

        gamesServicesImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlayServicesInCore.getInstance().getPlayServices().signOut();
                ScreenManager.getInstance().disposeScreen();
            }
        });

        final Container<Image> playContainer = new Container<Image>(gamesServicesImage);
        playContainer.setWidth(SETTINGS_ICON_SIZE);
        playContainer.setHeight(SETTINGS_ICON_SIZE);
        playContainer.pad(SETTINGS_ICON_PADDING);
        playContainer.setY(SETTINGS_ICON_SIZE);
        stage.addActor(playContainer);

        return gamesServicesImage;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(
                BACKGROUND_COLOR.r, BACKGROUND_COLOR.g,
                BACKGROUND_COLOR.b, BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        refreshGoldLabel();

        super.render(delta);
    }

    private void refreshGoldLabel() {
        goldLabel.setText("Gold: " + PlayServicesInCore.getInstance().getSnapshot().getGold());
        goldLabel.setSize(goldLabel.getPrefWidth(), goldLabel.getPrefHeight());
        goldLabel.setPosition(
                stage.getViewport().getWorldWidth() - CORNER_LABEL_MARGIN,
                stage.getViewport().getWorldHeight() - CORNER_LABEL_MARGIN,
                Align.topRight);
    }

}
