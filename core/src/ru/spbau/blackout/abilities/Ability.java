package ru.spbau.blackout.abilities;

import com.badlogic.gdx.scenes.scene2d.InputListener;

import ru.spbau.blackout.entities.Hero;


/**
 * Abstract class for hero abilities representation in game.
 */
public abstract class Ability {
    private final int level;  // it's final because the level can't be changed during the game.

    // Unfortunately, this field can't be final because in majority of cases
    // it's impossible to create listener before the object itself
    /** Defines icon behavior. */
    private InputListener iconInputListener;

    /** A hero who can cast this ability. */
    private final Hero hero;


    public Ability(InputListener iconInputListener, Hero hero, int level) {
        this.iconInputListener = iconInputListener;
        this.hero = hero;
        this.level = level;
    }


    /** Takes caster hero and duration of holding the icon. */
    public abstract void cast(float duration);
    /** Returns a path to its icon texture */
    public abstract String iconPath();

    // level
    public int getLevel() { return this.level; }

    // listener
    public InputListener getIconInputListener() { return this.iconInputListener; }
    protected void setIconInputListener(InputListener listener) { this.iconInputListener = listener; }
}
