package ru.spbau.blackout.ingameui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.entities.Hero;
import ru.spbau.blackout.ingameui.settings.AbilityIconSettings;
import ru.spbau.blackout.ingameui.settings.IngameUISettings;
import ru.spbau.blackout.screens.GameScreen;


public class IngameUI {
    private final Stage stage;
    private final Array<IngameUIObject> uiObjects = new Array<>();

    public IngameUI(GameScreen screen, IngameUISettings settings) {
        Camera camera = new OrthographicCamera();
        this.stage = new Stage(new ScreenViewport(camera), BlackoutGame.getInstance().getSpriteBatch());

        Gdx.input.setInputProcessor(this.stage);

        this.uiObjects.add(new Stick(screen.getServer(), settings.stickSettings));
        for (AbilityIconSettings iconSettings : settings.abilities) {
            this.uiObjects.add(new AbilityIcon(screen.getServer(), iconSettings));
        }
    }

    public void load(AssetManager assets) {
        for (IngameUIObject object : uiObjects) {
            object.load(assets);
        }
    }

    public void doneLoading(AssetManager assets, Hero character) {
        for (IngameUIObject object : uiObjects) {
            object.doneLoading(assets, stage, character);
        }
    }

    public void update(float deltaTime) {
        for (IngameUIObject object : uiObjects) {
            object.update(deltaTime);
        }
    }

    public void resize(int width, int height) {
        // TODO
        stage.getViewport().update(width, height);
    }

    public void draw() {
        stage.draw();
    }
}
