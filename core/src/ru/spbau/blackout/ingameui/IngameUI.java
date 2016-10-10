package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sun.glass.ui.View;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.screens.GameScreen;

import static ru.spbau.blackout.BlackoutGame.VIRTUAL_WORLD_HEIGHT;
import static ru.spbau.blackout.BlackoutGame.VIRTUAL_WORLD_WIDTH;

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

        stick = new Stick(stage);
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    public void draw() {
        stage.draw();
    }
}
