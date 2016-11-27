package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.ingameui.settings.AbilityIconSettings;
import ru.spbau.blackout.ingameui.settings.IngameUISettings;
import ru.spbau.blackout.screens.GameScreen;


public class IngameUI {
    private final GameScreen screen;
    private final Stage stage;
    private final OrthographicCamera camera;
    private final Stick stick;
    private final AbilityIcon[] abilities;

    public IngameUI(GameScreen screen, IngameUISettings settings) {
        this.screen = screen;
        this.camera = new OrthographicCamera();
        this.stage = new Stage(new ScreenViewport(this.camera), BlackoutGame.getInstance().getSpriteBatch());
        Gdx.input.setInputProcessor(this.stage);

        this.stick = new Stick(screen.getServer(), settings.stickSettings);
        this.abilities = new AbilityIcon[settings.abilities.length];
        for (int i = 0; i < this.abilities.length; ++i) {
            this.abilities[i] = new AbilityIcon(screen.getServer(), settings.abilities[i]);
        }
    }

    public void load(AssetManager assets) {
        stick.load(assets);
        for (AbilityIcon icon : abilities) {
            icon.load(assets);
        }
    }

    public void doneLoading(AssetManager assets, Hero character) {
        stick.doneLoading(assets, stage, character);
        for (AbilityIcon icon : abilities) {
            icon.doneLoading(assets, stage, character);
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    public void draw() {
        stage.draw();
    }
}
