package ru.spbau.blackout.abilities.gravity;

import com.badlogic.gdx.math.Vector2;

<<<<<<< HEAD
=======
import java.util.HashSet;
import java.util.Set;

import ru.spbau.blackout.abilities.fireball.FireballObject;
>>>>>>> 3749634... Gravity first version implemented
import ru.spbau.blackout.abilities.simpleshell.SimpleShellObject;
import ru.spbau.blackout.entities.Damageable;
import ru.spbau.blackout.entities.DynamicObject;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.utils.Uid;

import static ru.spbau.blackout.abilities.gravity.GravityAbility.CAST_SOUND_PATH;
import static ru.spbau.blackout.abilities.gravity.GravityAbility.EFFECT_PATH;
<<<<<<< HEAD
import static ru.spbau.blackout.abilities.gravity.GravityAbility.RADIUS;
=======
import static ru.spbau.blackout.abilities.gravity.GravityAbility.SHELL_RADIUS;
>>>>>>> 3749634... Gravity first version implemented
import static ru.spbau.blackout.abilities.gravity.GravityAbility.SHELL_MASS;
import static ru.spbau.blackout.abilities.gravity.GravityAbility.TIME_TO_LIVE;
import static ru.spbau.blackout.utils.Utils.isZeroVec;

public final class GravityObject extends SimpleShellObject {

<<<<<<< HEAD
=======
    private final Set<GameObject> captured = new HashSet<>();
    private /*final*/ GameObject caster;

>>>>>>> 3749634... Gravity first version implemented
    protected GravityObject(Definition def, Uid uid, float x, float y) {
        super(def, uid, x, y);
    }

<<<<<<< HEAD
    @Override
    public void updateState(float delta) {
        super.updateState(delta);
        for (GameObject go : getDef().getContext().gameWorld().getGameObjects()) {
            if (!go.isDead() && go instanceof Damageable) {
                // FIXME: scale damage with regard to distance
                float maxDamage = ((Definition) getDef()).damage;
                ((Damageable) go).damage(maxDamage * effectScale(go));
            }
=======
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
>>>>>>> 3749634... Gravity first version implemented
        }
    }

    @Override
<<<<<<< HEAD
    public void updateForFirstStep() {
        super.updateForFirstStep();
        for (GameObject go : getDef().getContext().gameWorld().getGameObjects()) {
            if (go instanceof DynamicObject) {
                System.out.println(go.getPosition());
                Vector2 offset = getPosition().cpy().sub(go.getPosition());
                if (!isZeroVec(offset)) {
                    float maxForce = ((Definition) getDef()).maxForce;
                    if (go.getUid().equals(new Uid(1))) {
                        System.out.println(getPosition());
                        System.out.println(go.getPosition());
                        System.out.println(offset);
                        System.out.println(offset.cpy().scl(maxForce * effectScale(go) / offset.len()));
                        System.out.println("");
                    }
                    ((DynamicObject) go).applyImpulse(offset.scl(maxForce * effectScale(go) / offset.len()));
=======
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
>>>>>>> 3749634... Gravity first version implemented
                }
            }
        }
    }

<<<<<<< HEAD
    private float effectScale(GameObject go) {
        return Math.max(0f, (RADIUS - getPosition().dst(go.getPosition())) / RADIUS);
    }

    public static final class Definition extends SimpleShellObject.Definition {

        public float maxForce;
        public GameObject caster;

        public Definition() {
            super(null, null, null, CAST_SOUND_PATH, SHELL_MASS);
=======
    public static final class Definition extends SimpleShellObject.Definition {

        private float force;

        public Definition() {
            super(null, SHELL_RADIUS, null, CAST_SOUND_PATH, SHELL_MASS);
>>>>>>> 3749634... Gravity first version implemented
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
<<<<<<< HEAD
=======

        public void setForce(float force) { this.force = force; }
        public float getForce() { return force; }
>>>>>>> 3749634... Gravity first version implemented
    }
}
