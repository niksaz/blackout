package ru.spbau.blackout.abilities;


import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.java8features.Optional;
import ru.spbau.blackout.network.UIServer;

/**
 * Abstract class for abilities which should be casted by single tap on the icon.
 */
public abstract class SimpleInstantAbility extends Ability {
    private transient Optional<Sound> /*final*/ startSound = Optional.empty();
    public static final float CAST_DISTANCE = 1.5f;  // FIXME: use unit radius


    public SimpleInstantAbility(int level) {
        super(level);
    }


    public abstract String castSoundPath();


    @Override
    public final void onCastStart(UIServer server) {
        startSound.get().play(1f /*FIXME: use sound volume from settings*/);

        Vector2 direction = new Vector2(1, 0).rotateRad(getUnit().getRotation());
        Vector2 target = new Vector2(getUnit().getPosition());
        target.mulAdd(direction, CAST_DISTANCE);

        server.sendAbilityCast(getUnit(), getUnit().getAbilityNum(this), target);
    }

    @Override
    public final void inCast(UIServer server, float delta) { /* nothing */ }

    @Override
    public final void onCastEnd(UIServer server) { /*nothing*/ }


    @Override
    public void load(GameContext context) {
        super.load(context);
        context.getAssets().load(castSoundPath(), Sound.class);
    }

    @Override
    public void doneLoading(GameContext context) {
        super.doneLoading(context);
        startSound = Optional.of(context.getAssets().get(castSoundPath(), Sound.class));
    }
}
