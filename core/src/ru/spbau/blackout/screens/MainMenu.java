package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
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

    private Stage stage;

    public MainMenu(BlackoutGame blackoutGame) {
        super(blackoutGame);

        stage = new Stage(new ExtendViewport(Constants.VIRTUAL_WORLD_WIDTH, Constants.VIRTUAL_WORLD_HEIGHT));

        Table middleTable = new Table();
        addBlackoutLabel(middleTable);

        Drawable buttonImage =
                new TextureRegionDrawable(new TextureRegion(new Texture(Constants.MAIN_MENU_BUTTON_PATH)));
        addButton(middleTable, Constants.MAIN_MENU_BUTTON_PLAY_TEXT, buttonImage);
        addButton(middleTable, Constants.MAIN_MENU_BUTTON_SHOP_TEXT, buttonImage);
        addButton(middleTable, Constants.MAIN_MENU_BUTTON_LEADERBOARD_TEXT, buttonImage);

        middleTable.setFillParent(true);
        stage.addActor(middleTable);
    }

    private void addBlackoutLabel(Table table) {
        BitmapFont font = new BitmapFont();
        font.getData().scale(Constants.MAIN_MENU_BLACKOUT_LABEL_SCALE);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Label.LabelStyle style = new Label.LabelStyle(font, Constants.MAIN_MENU_BLACKOUT_LABEL_COLOR);
        Label label = new Label(Constants.BLACKOUT_TEXT, style);

        table.add(label).pad(Constants.MAIN_MENU_BLACKOUT_LABEL_BOTTOM_PADDING).row();
    }

    private void addButton(Table table, String text, Drawable image) {
        BitmapFont font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        TextButtonStyle style = new TextButtonStyle();
        style.font = font;
        style.up = image;
        style.down = image;

        TextButton button = new TextButton(text, style);

        table.add(button).pad(Constants.MAIN_MENU_BUTTON_PADDING).row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(
                Constants.MAIN_MENU_BACKGROUND_COLOR.r,
                Constants.MAIN_MENU_BACKGROUND_COLOR.g,
                Constants.MAIN_MENU_BACKGROUND_COLOR.b,
                Constants.MAIN_MENU_BACKGROUND_COLOR.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
