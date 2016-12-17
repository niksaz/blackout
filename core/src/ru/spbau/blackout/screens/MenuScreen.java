package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import ru.spbau.blackout.BlackoutGame;

public class MenuScreen extends StageScreen {

    private static final Color BACKGROUND_COLOR = new Color(0.2f, 0.2f, 0.2f, 1.0f);

    private static final String SETTINGS_TEXTURE_PATH = "images/menuscreen/settings.png";
    private static final String GAME_SERVICES_TEXTURE_PATH = "images/menuscreen/games_controller_grey.png";
    private static final String GOLD_COIN_PATH_PREFIX = "images/menuscreen/goldCoin";
    private static final String BLACKOUT_TEXT = "Blackout";
    private static final String BLACKOUT_LABEL_STYLE_NAME = "blackout";

    private static final int GOLD_COIN_FRAMES = 9;

    private static final float COIN_IMAGE_SIZE_EXTENSION = 5.0f;
    private static final float ANIMATION_FRAME_DURATION = 0.25f;

    private static final float BLACKOUT_LABEL_BOTTOM_PADDING = 25.0f;
    private static final float BUTTON_WIDTH = 450.0f;
    private static final float BUTTON_HEIGHT = 50.0f;
    private static final float BUTTON_PADDING = 10.0f;
    private static final float CORNERS_MARGIN = 20.0f;
    private static final float SETTINGS_ICON_SIZE = 128.0f;
    private static final float SETTINGS_ICON_PADDING = 12.0f;

    private Label goldLabel;
    private Table middleTable;
    private Animation animation;
    private float stateTime;
    private Image coinImage;

    public MenuScreen() {
        super();

        addLeftPaneElements();
        addRightPaneElements();
        changeMiddleTable(MainMenuTable.getTable(this));
    }

    public void changeMiddleTable(Table table) {
        if (middleTable != null) {
            middleTable.remove();
        }
        middleTable = table;
        stage.addActor(middleTable);
    }

    private void addLeftPaneElements() {
        addLabelWithTextAt(
                "Hello, " + BlackoutGame.get().getPlayerEntity().getName(),
                CORNERS_MARGIN,
                stage.getViewport().getWorldHeight() - CORNERS_MARGIN,
                Align.topLeft);
        final Actor playServicesIcon = addGooglePlayGamesServicesIcon();
        addSettingsIcon(playServicesIcon);
    }

    private void addRightPaneElements() {
        goldLabel = addLabelWithTextAt("0", 0, 0, 0);

        final TextureRegion[] coinTextures = new TextureRegion[GOLD_COIN_FRAMES];
        for (int i = 0; i < GOLD_COIN_FRAMES; i++)  {
            final Texture coinTexture = new Texture(GOLD_COIN_PATH_PREFIX + i + ".png");
            coinTextures[i] = new TextureRegion(coinTexture);
        }
        animation = new Animation(ANIMATION_FRAME_DURATION, coinTextures);

        coinImage = new Image();
        final float square = goldLabel.getHeight() + COIN_IMAGE_SIZE_EXTENSION;
        coinImage.setSize(square, square);
        coinImage.setPosition(
                stage.getViewport().getWorldWidth() - CORNERS_MARGIN,
                stage.getViewport().getWorldHeight() - CORNERS_MARGIN,
                Align.topRight);
        stage.addActor(coinImage);
    }

    static TextButton addButton(Table table, String text, EventListener listener) {
        final TextButton button = new TextButton(text, BlackoutGame.get().assets().getDefaultSkin());
        if (listener != null) {
            button.addListener(listener);
        }
        table.add(button).pad(BUTTON_PADDING).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).row();
        return button;
    }

    static Label addBlackoutLabel(Table table) {
        final Label label = new Label(
                BLACKOUT_TEXT,
                BlackoutGame.get().assets().getDefaultSkin(),
                BLACKOUT_LABEL_STYLE_NAME);
        table.add(label).pad(BLACKOUT_LABEL_BOTTOM_PADDING).row();
        return label;
    }

    private Label addLabelWithTextAt(CharSequence text, float x, float y, int align) {
        final Label label = new Label(text, BlackoutGame.get().assets().getDefaultSkin());
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

        final Container<Image> settingsContainer = new Container<>(settingsImage);
        settingsContainer.setWidth(SETTINGS_ICON_SIZE);
        settingsContainer.setHeight(SETTINGS_ICON_SIZE);
        settingsContainer.pad(SETTINGS_ICON_PADDING);
        stage.addActor(settingsContainer);

        return settingsImage;
    }

    private Image addGooglePlayGamesServicesIcon() {
        final Texture gamesServices = new Texture(GAME_SERVICES_TEXTURE_PATH);
        final Image gamesServicesImage = new Image(gamesServices);
        gamesServicesImage.setVisible(false);

        gamesServicesImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BlackoutGame.get().playServicesInCore().getPlayServices().signOut();
                BlackoutGame.get().screenManager().disposeScreen();
            }
        });

        final Container<Image> playContainer = new Container<>(gamesServicesImage);
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

        stateTime += delta;
        coinImage.setDrawable(new TextureRegionDrawable(animation.getKeyFrame(stateTime, true)));

        refreshGoldLabel();

        super.render(delta);
    }

    private void refreshGoldLabel() {
        goldLabel.setText(String.valueOf(BlackoutGame.get().getPlayerEntity().getGold()));
        goldLabel.setSize(goldLabel.getPrefWidth(), goldLabel.getPrefHeight());
        goldLabel.setPosition(
                coinImage.getX(),
                stage.getViewport().getWorldHeight() - CORNERS_MARGIN,
                Align.topRight);
    }
}
