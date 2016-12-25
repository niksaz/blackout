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
public class HealthBarEffect implements GraphicEffect {
    private final Character character;
    private final SimpleProgressBar healthBar;
    private final Camera camera;


    public HealthBarEffect(Character character, SimpleProgressBar healthBar, GameContext context) {
        this.character = character;
        this.healthBar = healthBar;
        this.camera = context.getScreen().getCamera();
    }

    @Override
    public void update(float deltaTime) {
        this.healthBar.setValue(this.character.getHealth() / this.character.getMaxHealth());

        Vector3 realPos = camera.project(this.character.getOverHeadPivot());

        this.healthBar.setPosition(
                Vpx.fromRpx(realPos.x) - this.healthBar.getWidth() / 2,
                Vpx.fromRpx(realPos.y)
        );
    }

    @Override
    public void remove(GameContext context) {
        this.healthBar.remove();
    }
}
