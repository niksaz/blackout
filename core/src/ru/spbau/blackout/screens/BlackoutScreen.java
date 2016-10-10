package ru.spbau.blackout.screens;

import com.badlogic.gdx.Screen;

import ru.spbau.blackout.BlackoutGame;

public abstract class BlackoutScreen implements Screen {

    final protected BlackoutGame game;

    BlackoutScreen(BlackoutGame game) {
        this.game = game;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }

    public BlackoutGame getGame() {
        return game;
    }

}
