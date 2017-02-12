package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
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
import com.badlogic.gdx.utils.Array;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.settings.GameSettings;

/**
 * The part of menu ui which is used throughout all middle tables.
 */
public class MenuScreen extends StageScreen {

    public static final float BUTTON_HEIGHT = 50.0f;
    public static final float BUTTON_PADDING = 10.0f;
    public static final String BACK_TEXT = "Back";

    private static final String UP_ARROW_PATH = "images/menuscreen/up-arrow.png";
    private static final String DOWN_ARROW_PATH = "images/menuscreen/down-arrow.png";
    private static final String RATING_ICON_PATH = "images/menuscreen/ratingIcon.png";
    private static final String GAMES_ACHIEVEMENTS_GAMES_PATH = "images/game_services/games_achievements.png";
    private static final String GAMES_CONTROLLER_GAMES_PATH = "images/game_services/games_controller_grey.png";
    private static final String GAMES_LEADERBOARDS_GAMES_PATH = "images/game_services/games_leaderboards.png";
    private static final String GOLD_COIN_PATH_PREFIX = "images/menuscreen/goldCoin";
    private static final String MENU_MUSIC_PATH = "music/menu/town.mp3";

    private static final String BLACKOUT_TEXT = "Blackout";
    private static final String BLACKOUT_LABEL_STYLE_NAME = "blackout";

    private static final int GOLD_COIN_FRAMES = 9;

    private static final float ICON_IMAGE_SIZE_EXTENSION = 5.0f;
    private static final float ANIMATION_FRAME_DURATION = 0.25f;

    private static final float BLACKOUT_LABEL_BOTTOM_PADDING = 25.0f;
    private static final float BUTTON_WIDTH = 450.0f;
    private static final float CORNERS_MARGIN = 20.0f;
    private static final float SETTINGS_ICON_SIZE = 110.0f;
    private static final float SETTINGS_ICON_PADDING = 10.0f;

    private Table middleTable;
    private final Music menuMusic;

    public MenuScreen() {
        super();

        menuMusic = Gdx.audio.newMusic(Gdx.files.internal(MENU_MUSIC_PATH));
        menuMusic.setLooping(true);
        updateMusicVolume();

        addLeftPaneElements();
        addRightPaneElements();
        changeMiddleTable(ru.spbau.blackout.screens.tables.MainMenuTable.getTable(this));
    }

    public void changeMiddleTable(Table table) {
        if (middleTable != null) {
            middleTable.remove();
        }
        table.setFillParent(true);
        middleTable = table;
        stage.addActor(middleTable);
    }

    private void addLeftPaneElements() {
        final Label greetingsLabel = addLabelWithTextAt(
                "Greetings, " + BlackoutGame.get().getPlayerEntity().getName(),
                CORNERS_MARGIN,
                stage.getViewport().getWorldHeight() - CORNERS_MARGIN,
                Align.topLeft);

        final float squareIconSize = greetingsLabel.getHeight() + ICON_IMAGE_SIZE_EXTENSION;
        final Image ratingImage = new Image(new Texture(RATING_ICON_PATH));
        ratingImage.setSize(squareIconSize, squareIconSize);
        ratingImage.setPosition(
                CORNERS_MARGIN,
                stage.getViewport().getWorldHeight() - 2 * CORNERS_MARGIN - greetingsLabel.getHeight(),
                Align.topLeft);
        stage.addActor(ratingImage);

        final Label ratingLabel = new Label("0", BlackoutGame.get().assets().getDefaultSkin()) {
            @Override
            public void act(float delta) {
                setText(String.valueOf((int) BlackoutGame.get().getPlayerEntity().getRating()));
                setSize(getPrefWidth(), getPrefHeight());
                setPosition(
                        CORNERS_MARGIN + squareIconSize + CORNERS_MARGIN,
                        stage.getViewport().getWorldHeight() - 2 * CORNERS_MARGIN - greetingsLabel.getHeight(),
                        Align.topLeft);
                super.act(delta);
            }
        };
        stage.addActor(ratingLabel);

        final Array<Actor> playServicesIcons = addGooglePlayGamesServicesIcons();
        addSettingsIcon(playServicesIcons);
    }

    private void addRightPaneElements() {
        final TextureRegion[] coinTextures = new TextureRegion[GOLD_COIN_FRAMES];
        for (int i = 0; i < GOLD_COIN_FRAMES; i++)  {
            final Texture coinTexture = new Texture(GOLD_COIN_PATH_PREFIX + i + ".png");
            coinTextures[i] = new TextureRegion(coinTexture);
        }
        final Animation animation = new Animation(ANIMATION_FRAME_DURATION, coinTextures);

        final Image coinImage = new Image() {
            private float stateTime = 0;

            @Override
            public void act(float delta) {
                stateTime += delta;
                setDrawable(new TextureRegionDrawable(animation.getKeyFrame(stateTime, true)));
                super.act(delta);
            }
        };

        final Label goldLabel = new Label("0", BlackoutGame.get().assets().getDefaultSkin()) {
            @Override
            public void act(float delta) {
                setText(String.valueOf(BlackoutGame.get().getPlayerEntity().getCurrentCoins()));
                setSize(getPrefWidth(), getPrefHeight());
                setPosition(
                        coinImage.getX(),
                        stage.getViewport().getWorldHeight() - CORNERS_MARGIN,
                        Align.topRight);
                super.act(delta);
            }
        };
        stage.addActor(goldLabel);

        final float square = goldLabel.getHeight() + ICON_IMAGE_SIZE_EXTENSION;
        coinImage.setSize(square, square);
        coinImage.setPosition(
                stage.getViewport().getWorldWidth() - CORNERS_MARGIN,
                stage.getViewport().getWorldHeight() - CORNERS_MARGIN,
                Align.topRight);
        stage.addActor(coinImage);
    }

    public TextButton addBackToMainMenuButton(Table table) {
        return addBackToMainMenuButton(table, 1);
    }

    public TextButton addBackToMainMenuButton(Table table, int columns) {
        return addButton(
                table,
                BACK_TEXT,
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        changeMiddleTable(ru.spbau.blackout.screens.tables.MainMenuTable.getTable(MenuScreen.this));
                    }
                },
                columns
        );
    }

    public static TextButton addButton(Table table, String text, EventListener listener) {
        return addButton(table, text, listener, 1);
    }

    public static TextButton addButton(Table table, String text, EventListener listener, int columns) {
        final TextButton button = new TextButton(text, BlackoutGame.get().assets().getDefaultSkin());
        if (listener != null) {
            button.addListener(listener);
        }
        table.add(button).colspan(columns).pad(BUTTON_PADDING).width(BUTTON_WIDTH).height(BUTTON_HEIGHT).row();
        return button;
    }

    public static Label addBlackoutLabel(Table table) {
        return addBlackoutLabel(table, 1);
    }

    public static Label addBlackoutLabel(Table table, int columns) {
        final Label label = new Label(
                BLACKOUT_TEXT,
                BlackoutGame.get().assets().getDefaultSkin(),
                BLACKOUT_LABEL_STYLE_NAME);
        table.add(label).colspan(columns).pad(BLACKOUT_LABEL_BOTTOM_PADDING).row();

        // FIXME: a way to get free gold. :)
        label.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BlackoutGame.get().getPlayerEntity().changeGold(50);
            }
        });

        return label;
    }

    private Label addLabelWithTextAt(CharSequence text, float x, float y, int align) {
        final Label label = new Label(text, BlackoutGame.get().assets().getDefaultSkin());
        label.setPosition(x, y, align);
        stage.addActor(label);
        return label;
    }

    private Image addSettingsIcon(final Array<Actor> controlledImages) {
        final Texture settingsTexture = new Texture(UP_ARROW_PATH);
        final Image settingsImage = new Image(settingsTexture);

        settingsImage.addListener(new ClickListener() {
            private boolean currentVisibility = false;
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentVisibility ^= true;
                for (Actor actor : controlledImages) {
                    actor.setVisible(currentVisibility);
                }
                if (currentVisibility) {
                    settingsImage.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(DOWN_ARROW_PATH))));
                } else {
                    settingsImage.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(UP_ARROW_PATH))));
                }
            }
        });

        final Container<Image> settingsContainer = new Container<>(settingsImage);
        settingsContainer.setWidth(SETTINGS_ICON_SIZE);
        settingsContainer.setHeight(SETTINGS_ICON_SIZE);
        settingsContainer.pad(SETTINGS_ICON_PADDING);
        stage.addActor(settingsContainer);

        return settingsImage;
    }

    private Array<Actor> addGooglePlayGamesServicesIcons() {
        final Array<Actor> icons = new Array<>();
        icons.add(addGooglePlayGamesServicesIcon(
                GAMES_CONTROLLER_GAMES_PATH,
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        BlackoutGame.get().playServicesInCore().getPlayServices().signOut();
                        BlackoutGame.get().screenManager().disposeScreen();
                    }
                },
                3 * (SETTINGS_ICON_SIZE - SETTINGS_ICON_PADDING))
        );
        icons.add(addGooglePlayGamesServicesIcon(
                GAMES_ACHIEVEMENTS_GAMES_PATH,
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        BlackoutGame.get().playServicesInCore().getPlayServices().showAchievements();
                    }
                },
                2 * (SETTINGS_ICON_SIZE - SETTINGS_ICON_PADDING))
        );
        icons.add(addGooglePlayGamesServicesIcon(
                GAMES_LEADERBOARDS_GAMES_PATH,
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        BlackoutGame.get().playServicesInCore().getPlayServices().showLeaderboards();
                    }
                },
                1 * (SETTINGS_ICON_SIZE - SETTINGS_ICON_PADDING))
        );
        return icons;
    }

    private Actor addGooglePlayGamesServicesIcon(String path, EventListener listener, float y) {
        final Texture iconTexture = new Texture(path);
        final Image iconImage = new Image(iconTexture);
        iconImage.setVisible(false);
        iconImage.addListener(listener);

        final Container<Image> container = new Container<>(iconImage);
        container.setWidth(SETTINGS_ICON_SIZE);
        container.setHeight(SETTINGS_ICON_SIZE);
        container.pad(SETTINGS_ICON_PADDING);
        container.setY(y);
        stage.addActor(container);

        return iconImage;
    }

    @Override
    public void render(float delta) {
        final Color color = BlackoutGame.get().assets().getBackgroundColor();
        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        super.render(delta);
    }

    @Override
    public void show() {
        menuMusic.setPosition(0);
        menuMusic.play();
        super.show();
    }

    @Override
    public void hide() {
        menuMusic.pause();
    }

    @Override
    public void dispose() {
        menuMusic.dispose();
        super.dispose();
    }

    public void updateMusicVolume() {
        menuMusic.setVolume(
                GameSettings.MUSIC_MAX_VOLUME * BlackoutGame.get().getPlayerEntity().getGameSettings().musicVolume);
    }
}
