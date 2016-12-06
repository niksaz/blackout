package ru.spbau.blackout.abilities;


import com.badlogic.gdx.audio.Sound;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.entities.GameUnit;
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
            sound.play(BlackoutGame.get().context().getSettings().effectsVolume);
        });
        this.cast();
        this.chargeStart();
    }

    @Override
    public final void inCast(float deltaTime) { /*nothing*/ }

    @Override
    public final void onCastEnd() { /*nothing*/ }


    @Override
    public void load() {
        super.load();
        BlackoutGame.get().context().getAssets().load(this.castSoundPath(), Sound.class);
    }

    @Override
    public void doneLoading() {
        super.doneLoading();
        this.startSound = Optional.of(BlackoutGame.get().context().getAssets().get(this.castSoundPath(), Sound.class));
    }
}
