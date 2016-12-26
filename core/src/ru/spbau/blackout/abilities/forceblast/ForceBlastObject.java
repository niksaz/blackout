package ru.spbau.blackout.abilities.forceblast;

import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.entities.AbilityObject;
import ru.spbau.blackout.entities.Damageable;
import ru.spbau.blackout.entities.DynamicObject;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.shapescreators.CircleCreator;
import ru.spbau.blackout.utils.Utils;

import static ru.spbau.blackout.abilities.forceblast.ForceBlastAbility.CAST_SOUND_PATH;
import static ru.spbau.blackout.abilities.forceblast.ForceBlastAbility.EXPLOSION_EFFECT_PATH;
import static ru.spbau.blackout.abilities.forceblast.ForceBlastAbility.IMPULSE;
import static ru.spbau.blackout.abilities.forceblast.ForceBlastAbility.RADIUS;


public class ForceBlastObject extends AbilityObject {

    boolean livesOnlyOneStep;

    public ForceBlastObject(Definition def, long uid, float x, float y) {
        super(def, uid, x, y);
    }

    @Override
    public void beginContact(GameObject object) {
        super.beginContact(object);

        if (Utils.vecEquals(object.getPosition(), getPosition())) {
            return;
        }

        if (object instanceof DynamicObject) {
            Vector2 impulse = object.getPosition().cpy().sub(getPosition());
            impulse.scl(IMPULSE / impulse.len());
            ((DynamicObject) object).applyImpulse(impulse);
        }

        if (object instanceof Damageable) {
            ((Damageable) object).damage(((Definition) getDef()).damage);
        }
    }

    @Override
    public void updateState(float delta) {
        super.updateState(delta);
        if (livesOnlyOneStep && !isDead()) {
            kill();
        } else {
            livesOnlyOneStep = true;
        }
    }

    public static class Definition extends AbilityObject.Definition {

        private static final long serialVersionUID = 1000000000L;

        public float damage;

        public Definition() {
            super(null, new CircleCreator(RADIUS));
        }

        @Override
        public GameObject makeInstance(long uid, float x, float y) {
            return new ForceBlastObject(this, uid, x, y);
        }

        @Override
        protected String castSoundPath() {
            return CAST_SOUND_PATH;
        }

        @Override
        protected String deathEffectPath() {
            return EXPLOSION_EFFECT_PATH;
        }
    }
}
