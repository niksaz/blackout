package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ru.spbau.blackout.screens.GameScreen;


public class IngameUI {
    GameScreen screen;
    Stage stage;
    OrthographicCamera camera;
    Stick stick;

    public IngameUI(GameScreen screen) {
        this.screen = screen;
        camera = new OrthographicCamera();
        stage = new Stage(new ScreenViewport(camera), screen.getGame().spriteBatch);
        Gdx.input.setInputProcessor(stage);

        stick = new Stick(screen.getHero());
    }

    public void load(AssetManager assets) {
        stick.load(assets);
    }

    public void doneLoading(AssetManager assets) {
        stick.doneLoading(assets, stage);
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    public void draw() {
        stage.draw();
    }
}
