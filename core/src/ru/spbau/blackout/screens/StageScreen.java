package ru.spbau.blackout.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import ru.spbau.blackout.BlackoutGame;

abstract class StageScreen extends BlackoutScreen {

    protected Stage stage;

    public StageScreen(BlackoutGame game) {
        super(game);
        stage = new Stage(new ExtendViewport(BlackoutGame.VIRTUAL_WORLD_WIDTH, BlackoutGame.VIRTUAL_WORLD_HEIGHT));
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }

}
