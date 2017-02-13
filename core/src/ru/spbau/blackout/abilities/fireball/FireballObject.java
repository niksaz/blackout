package ru.spbau.blackout.abilities.fireball;

import ru.spbau.blackout.abilities.simpleshell.SimpleShellObject;
import ru.spbau.blackout.entities.Damageable;
import ru.spbau.blackout.entities.DynamicObject;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.shapescreators.CircleCreator;
import ru.spbau.blackout.utils.Uid;

import static ru.spbau.blackout.abilities.fireball.FireballAbility.CAST_SOUND_PATH;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.EXPLOSION_EFFECT_PATH;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.FIRE_EFFECT_PATH;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.IMPULSE_FACTOR;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.SHELL_MASS;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.SHELL_RADIUS;
import static ru.spbau.blackout.abilities.fireball.FireballAbility.TIME_TO_LIVE;


public final class FireballObject extends SimpleShellObject {

    protected FireballObject(FireballObject.Definition def, Uid uid, float x, float y) {
        super(def, uid, x, y);
    }

    @Override
    public void beginContact(GameObject object) {
        if (object instanceof DynamicObject) {
            DynamicObject dynamic = (DynamicObject) object;
            // This object is going to dye. So, we don't care about changes of its velocity.
            velocity.add(body.getLinearVelocity()).scl(IMPULSE_FACTOR);
            dynamic.applyImpulse(velocity);
        }

        if (object instanceof Damageable) {
            ((Damageable) object).damage(((Definition) getDef()).damage);
        }

        kill();
    }

    /**
     * Additionally defines timeToLive for an object.
     */
    public final static class Definition extends SimpleShellObject.Definition {

        private static final long serialVersionUID = 1000000000L;

        public Definition() {
            super(null, new CircleCreator(SHELL_RADIUS), EXPLOSION_EFFECT_PATH, CAST_SOUND_PATH, SHELL_MASS);
        }

        @Override
        public GameObject makeInstance(Uid uid, float x, float y) {
            return new FireballObject(this, uid, x, y);
        }

        @Override
        protected final String liveEffectPath() { return FIRE_EFFECT_PATH; }
        @Override
        protected final String explosionEffectPath() { return EXPLOSION_EFFECT_PATH; }
        @Override
        protected final float timeToLive() { return TIME_TO_LIVE; }
    }
}
