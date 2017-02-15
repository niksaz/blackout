package ru.spbau.blackout.graphiceffects;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.progressbar.SimpleProgressBar;
import ru.spbau.blackout.units.Vpx;


/**
 * A small red health bar under the head of the character.
 * This effect needs to be loaded.
 */
public final class HealthBarEffect extends GraphicEffect {

    private final Character character;
    private final SimpleProgressBar healthBar;
    private final Camera camera;

    public static void create(Character character, SimpleProgressBar healthBar, GameContext context) {
        character.addGraphicEffect(new HealthBarEffect(character, healthBar, context));
    }

    private HealthBarEffect(Character character, SimpleProgressBar healthBar, GameContext context) {
        super(character);
        this.character = character;
        this.healthBar = healthBar;
        camera = context.getScreen().getCamera();
    }

    @Override
    public void update(float deltaTime) {
        healthBar.setValue(character.getHealth() / character.getMaxHealth());

        Vector3 realPos = camera.project(character.getOverHeadPivot());

        healthBar.setPosition(
                Vpx.fromRpx(realPos.x) - healthBar.getWidth() / 2,
                Vpx.fromRpx(realPos.y)
        );
    }

    @Override
    public void dispose(GameContext context) {
        super.remove(context);
        healthBar.remove();
    }
}
