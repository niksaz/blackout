package ru.spbau.blackout.abilities;

import com.badlogic.gdx.scenes.scene2d.InputListener;

import ru.spbau.blackout.entities.Hero;


/**
 * Abstract class for hero abilities.
 */
public abstract class Ability {
    private int level = 0;
    private InputListener iconInputListener;
    private Hero hero;

    public Ability(InputListener iconInputListener, Hero hero, int level) {
        this.iconInputListener = iconInputListener;
        this.hero = hero;
        this.level = level;
    }

    /**
     * Takes caster hero and duration of holding the icon.
     */
    public abstract void cast(float duration);

    public abstract String iconPath();

    // level
    public void setLevel(int newLevel) { this.level = newLevel; }
    public void incLevel() { this.level += 1; }
    public int getLevel() { return this.level; }

    // listener
    public InputListener getIconInputListener() { return this.iconInputListener; }
    protected void setIconInputListener(InputListener listener) { this.iconInputListener = listener; }
}
