package ru.spbau.blackout.abilities;


import com.badlogic.gdx.audio.Sound;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.java8features.Optional;

/**
 * Abstract class for abilities which should be casted by single tap on the icon.
 */
public abstract class SimpleInstantAbility extends Ability {
    private Optional<Sound> startSound = Optional.empty();


    public SimpleInstantAbility(int level) {
        super(level);
    }


    public abstract void cast();
    public abstract String castSoundPath();


    @Override
    public final void onCastStart() {
        this.startSound.ifPresent(sound -> {
            sound.play(1f /*FIXME: use sound volume from settings*/);
        });
        this.cast();
        this.chargeStart();
    }

    @Override
    public final void inCast(float deltaTime) { /*nothing*/ }

    @Override
    public final void onCastEnd() { /*nothing*/ }


    @Override
    public void load(GameContext context) {
        super.load(context);
        context.getAssets().load(this.castSoundPath(), Sound.class);
    }

    @Override
    public void doneLoading(GameContext context) {
        super.doneLoading(context);
        this.startSound = Optional.of(context.getAssets().get(this.castSoundPath(), Sound.class));
    }
}
