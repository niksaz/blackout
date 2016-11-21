package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.screens.GameScreen;


public class IngameUI {
    GameScreen screen;
    Stage stage;
    OrthographicCamera camera;
    Stick stick;

    public IngameUI(GameScreen screen, Settings settings) {
        this.screen = screen;
        camera = new OrthographicCamera();
        stage = new Stage(new ScreenViewport(camera), BlackoutGame.getInstance().spriteBatch);
        Gdx.input.setInputProcessor(stage);

        stick = new Stick(screen.getServer(), settings.stickSettings);
    }

    public void load(AssetManager assets) {
        stick.load(assets);
    }

    public void doneLoading(AssetManager assets, Hero character) {
        stick.doneLoading(assets, stage, character);
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    public void draw() {
        stage.draw();
    }

    public static class Settings {
        public Stick.Settings stickSettings = new Stick.Settings();
    }
}
