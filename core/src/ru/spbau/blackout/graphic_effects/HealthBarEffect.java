package ru.spbau.blackout.graphic_effects;

import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.progressbar.SimpleProgressBar;


/**
 * A small red health bar under the head of the unit.
 * This effect needs to be loaded.
 */
public class HealthBarEffect extends GraphicEffect {
    private final GameUnit unit;
    private final SimpleProgressBar healthBar;


    public HealthBarEffect(GameUnit unit, SimpleProgressBar healthBar) {
        this.unit = unit;
        this.healthBar = healthBar;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        this.healthBar.setValue(this.unit.getHealth() / this.unit.getMaxHealth());
    }
}
