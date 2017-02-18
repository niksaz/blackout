package ru.spbau.blackout.abilities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.Shape;

import org.jetbrains.annotations.Nullable;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.DynamicObject;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.StaticObject;
import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.utils.Uid;

import static ru.spbau.blackout.settings.GameSettings.SOUND_MAX_VOLUME;

public abstract class StaticAbilityObject extends StaticObject implements AbilityObject {

    protected StaticAbilityObject(Definition def, Uid uid, float x, float y) {
        super(def, uid, x, y);

        if (def.getCastSound() != null) {
            def.getCastSound().play(getDef().getContext().getSettings().soundVolume * SOUND_MAX_VOLUME);
        }
    }



    public static abstract class Definition extends StaticObject.Definition {

        private static final long serialVersionUID = 1000000000L;

        @Nullable
        private final String castSoundPath;
        @Nullable
        private  /*final*/ transient Sound castSound;

        public Definition(@Nullable String modelPath, Creator<Shape> shapeCreator,
                          @Nullable String deathEffectPath, @Nullable String castSoundPath) {
            super(modelPath, shapeCreator, deathEffectPath);
            this.castSoundPath = castSoundPath;
        }

        @Override
        public void load(GameContext context) {
            super.load(context);
            if (castSoundPath != null) {
                context.getAssets().load(castSoundPath, Sound.class);
            }
        }

        @Override
        public void doneLoading() {
            super.doneLoading();
            if (castSoundPath != null) {
                castSound = getContext().getAssets().get(castSoundPath, Sound.class);
            }
        }

        @Nullable
        public final Sound getCastSound() {
            return castSound;
        }
    }
}
