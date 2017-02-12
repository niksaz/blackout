package ru.spbau.blackout.abilities.gravity;

import com.badlogic.gdx.math.Vector2;

import java.util.HashSet;
import java.util.Set;

import ru.spbau.blackout.abilities.fireball.FireballObject;
import ru.spbau.blackout.abilities.simpleshell.SimpleShellObject;
import ru.spbau.blackout.entities.Damageable;
import ru.spbau.blackout.entities.DynamicObject;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.utils.Uid;

import static ru.spbau.blackout.abilities.gravity.GravityAbility.CAST_SOUND_PATH;
import static ru.spbau.blackout.abilities.gravity.GravityAbility.EFFECT_PATH;
import static ru.spbau.blackout.abilities.gravity.GravityAbility.SHELL_RADIUS;
import static ru.spbau.blackout.abilities.gravity.GravityAbility.SHELL_MASS;
import static ru.spbau.blackout.abilities.gravity.GravityAbility.TIME_TO_LIVE;
import static ru.spbau.blackout.utils.Utils.isZeroVec;

public final class GravityObject extends SimpleShellObject {

    private final Set<GameObject> captured = new HashSet<>();
    private /*final*/ GameObject caster;

    protected GravityObject(Definition def, Uid uid, float x, float y) {
        super(def, uid, x, y);
    }

    void setCaster(GameObject caster) { this.caster = caster; }

    @Override
    public void beginContact(GameObject object) {
        super.beginContact(object);
        if (object != caster) {
            captured.add(object);
        }
    }

    @Override
    public void endContact(GameObject object) {
        super.endContact(object);
        if (captured.contains(object)) {
            captured.remove(object);
        }
    }

    @Override
    public void updateState(float delta) {
        super.updateState(delta);
        for (GameObject go : captured) {
            Vector2 offset = getPosition().sub(go.getPosition());
            if (go instanceof Damageable) {
                // FIXME: scale damage with regard to distance
                ((Damageable) go).damage(((Definition) getDef()).damage);
            }
            if (go instanceof DynamicObject) {
                if (!isZeroVec(offset)) {
                    float force = ((Definition) getDef()).getForce();
                    ((DynamicObject) go).applyImpulse(offset.scl(force / offset.len()));
                }
            }
        }
    }

    public static final class Definition extends SimpleShellObject.Definition {

        private float force;

        public Definition() {
            super(null, SHELL_RADIUS, null, CAST_SOUND_PATH, SHELL_MASS);
        }

        @Override
        protected String liveEffectPath() { return EFFECT_PATH; }
        @Override
        protected String explosionEffectPath() { return null; }
        @Override
        protected float timeToLive() { return TIME_TO_LIVE; }

        @Override
        public GameObject makeInstance(Uid uid, float x, float y) {
            return new GravityObject(this, uid, x, y);
        }

        public void setForce(float force) { this.force = force; }
        public float getForce() { return force; }
    }
}
