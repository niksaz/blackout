package ru.spbau.blackout.screens;

import com.badlogic.gdx.Screen;

import ru.spbau.blackout.BlackoutGame;

public abstract class BlackoutScreen implements Screen {

    protected BlackoutGame blackoutGame;

    protected BlackoutScreen(BlackoutGame blackoutGame) {
        this.blackoutGame = blackoutGame;
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

}