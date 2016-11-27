package ru.spbau.blackout.abilities;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import ru.spbau.blackout.entities.Hero;

/**
 * Abstract class for abilities which should be casted by single tap on the icon.
 */
public abstract class InstantAbility extends Ability {
    public InstantAbility(Hero hero, int level) {
        super(null /*can't create new Listener here*/, hero, level);
        this.setIconInputListener(this.new Listener());
    }

    /**
     * Ability::cast with zero duration.
     */
    public abstract void cast();
    @Override
    public void cast(float duration) { this.cast(); }  // This type of abilities doesn't handle duration.

    /**
     * IconInputListener which handles only <code>touchDown</code> events and casts the ability.
     */
    private class Listener extends InputListener {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            InstantAbility.this.cast();

            // It means that I don't want this listener to receive all touchDrag and touchUp events,
            // even those not over this actor, until touchUp is received.
            return false;
        }
    }
}
