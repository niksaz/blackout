package ru.spbau.blackout.graphic_effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.progressbar.SimpleProgressBar;
import ru.spbau.blackout.units.Vpx;

import static ru.spbau.blackout.BlackoutGame.getWorldHeight;
import static ru.spbau.blackout.BlackoutGame.getWorldWidth;


/**
 * A small red health bar under the head of the unit.
 * This effect needs to be loaded.
 */
public class HealthBarEffect extends GraphicEffect {
    private final GameUnit unit;
    private final SimpleProgressBar healthBar;
    private final Camera camera;


    public HealthBarEffect(GameUnit unit, SimpleProgressBar healthBar, Camera camera) {
        this.unit = unit;
        this.healthBar = healthBar;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        this.healthBar.setValue(this.unit.getHealth() / this.unit.getMaxHealth());

        Vector3 realPos = camera.project(this.unit.getUnderHeadPivot());

        this.healthBar.setPosition(
                Vpx.fromRpx(realPos.x) - this.healthBar.getWidth() / 2,
                Vpx.fromRpx(realPos.y)
        );
    }
}
