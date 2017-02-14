package ru.spbau.blackout.abilities.gravity;

import com.badlogic.gdx.math.Vector2;

import java.io.Serializable;

import ru.spbau.blackout.abilities.simpleshell.SimpleShellObject;
import ru.spbau.blackout.entities.Damageable;
import ru.spbau.blackout.entities.DynamicObject;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.utils.Uid;

import static ru.spbau.blackout.abilities.gravity.GravityAbility.CAST_SOUND_PATH;
import static ru.spbau.blackout.abilities.gravity.GravityAbility.EFFECT_PATH;
import static ru.spbau.blackout.abilities.gravity.GravityAbility.RADIUS;
import static ru.spbau.blackout.abilities.gravity.GravityAbility.SHELL_MASS;
import static ru.spbau.blackout.abilities.gravity.GravityAbility.TIME_TO_LIVE;
import static ru.spbau.blackout.utils.Utils.isZeroVec;

public final class GravityObject extends SimpleShellObject {

    private final GameObject caster;
    private final float maxForce;
    private final float maxDamage;

    protected GravityObject(Definition def, Uid uid, float x, float y) {
        super(def, uid, x, y);
        caster = def.caster;
        maxForce = def.maxForce;
        maxDamage = def.damage;
    }

    @Override
    public void updateState(float delta) {
        super.updateState(delta);
        for (GameObject go : getDef().getContext().gameWorld().getGameObjects()) {
            if (go != caster && !go.isDead() && go instanceof Damageable) {
                ((Damageable) go).damage(maxDamage * powerFactor(go));
            }
        }
    }

    @Override
    public void updateBeforeSecondStep() {
        super.updateBeforeSecondStep();
        for (GameObject go : getDef().getContext().gameWorld().getGameObjects()) {
            if (go != caster && go instanceof DynamicObject) {
                Vector2 offset = getPosition().cpy().sub(go.getPosition());
                if (!isZeroVec(offset)) {
                    ((DynamicObject) go).applyTemporaryImpulse(offset.scl(maxForce * powerFactor(go) / offset.len()));
                }
            }
        }
    }

    private float powerFactor(GameObject go) {
        // f is linear
        // f(0) = 1
        // f(>= RADIUS) = 0
        float distance = getPosition().dst(go.getPosition());
        return Math.max(0f, 1f - distance / RADIUS);
    }

    public static final class Definition extends SimpleShellObject.Definition implements Serializable {

        public float maxForce;
        public transient GameObject caster;

        public Definition() {
            super(null, null, null, CAST_SOUND_PATH, SHELL_MASS);
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
    }
}
