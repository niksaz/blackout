package ru.spbau.blackout.ingameui.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Stage;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.ingameui.IngameUIObject;
import ru.spbau.blackout.progressbar.HorizontalProgressBar;
import ru.spbau.blackout.progressbar.SimpleProgressBar;

import static ru.spbau.blackout.BlackoutGame.getWorldHeight;
import static ru.spbau.blackout.BlackoutGame.getWorldWidth;


public final class HealthBar extends IngameUIObject {
    public static final String PATH_EMPTY = "images/health_bar/empty.png";
    public static final String PATH_FULL = "images/health_bar/full.png";
    public static final float WIDTH = getWorldWidth() * 0.22f;
    public static final float HEIGHT = getWorldHeight() * 0.045f;
    public static final float START_X = getWorldWidth() * 0.04f;
    public static final float START_Y = getWorldHeight() * (1 - 0.05f) - HEIGHT;


    private final SimpleProgressBar healthBar = new HorizontalProgressBar(PATH_EMPTY, PATH_FULL);
    private /*final*/ Character character;


    @Override
    public void load(GameContext context) {
        healthBar.load(context.getAssets());
    }

    @Override
    public void doneLoading(GameContext context, Stage stage, Character character) {
        this.character = character;

        healthBar.doneLoading(context.getAssets());
        healthBar.setPosition(START_X, START_Y);
        healthBar.setSize(WIDTH, HEIGHT);
        stage.addActor(healthBar);
    }

    @Override
    public void update(float deltaTime) {
        this.healthBar.setValue(character.getHealth() / character.getMaxHealth());
    }

    @Override
    public void dispose() {
        // TODO
    }
}
