package ru.spbau.blackout.abilities.forceblast;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Vector2;

import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.abilities.StaticAbilityObject;
import ru.spbau.blackout.entities.Character;
import ru.spbau.blackout.entities.Damageable;
import ru.spbau.blackout.entities.DynamicObject;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.graphiceffects.GradualScaleEffect;
import ru.spbau.blackout.shapescreators.CircleCreator;
import ru.spbau.blackout.utils.Particles;
import ru.spbau.blackout.utils.Uid;

import static ru.spbau.blackout.abilities.forceblast.ForceBlastAbility.CAST_SOUND_PATH;
import static ru.spbau.blackout.abilities.forceblast.ForceBlastAbility.EXPLOSION_EFFECT_PATH;
import static ru.spbau.blackout.abilities.forceblast.ForceBlastAbility.EXPLOSION_TIME;
import static ru.spbau.blackout.abilities.forceblast.ForceBlastAbility.IMPULSE;
import static ru.spbau.blackout.abilities.forceblast.ForceBlastAbility.MAX_SCALE;
import static ru.spbau.blackout.abilities.forceblast.ForceBlastAbility.MIN_SCALE;
import static ru.spbau.blackout.abilities.forceblast.ForceBlastAbility.MODEL_PATH;
import static ru.spbau.blackout.abilities.forceblast.ForceBlastAbility.RADIUS;
import static ru.spbau.blackout.abilities.forceblast.ForceBlastAbility.SCALE_TIME;


public final class ForceBlastObject extends StaticAbilityObject {

    private final Set<GameObject> damaged = new HashSet<>();
    private float timeToLive = EXPLOSION_TIME;

    protected ForceBlastObject(Definition def, Uid uid, float x, float y) {
        super(def, uid, x, y);

        GradualScaleEffect.create(this, MIN_SCALE, MAX_SCALE, SCALE_TIME);
    }

    void setCaster(Character caster) {
        setHeight(caster.getChestPivot().z);
        damaged.add(caster);  // do not damage caster
    }

    @Override
    public void beginContact(GameObject go) {
        if (!damaged.contains(go)) {
            damaged.add(go);

            if (go instanceof DynamicObject) {
                Vector2 impulse = go.getPosition().cpy().sub(getPosition());
                impulse.scl(IMPULSE / impulse.len());
                ((DynamicObject) go).applyImpulse(impulse);
            }

            if (go instanceof Damageable) {
                ((Damageable) go).damage(((Definition) getDef()).damage);
            }
        }
    }

    @Override
    public void updateState(float delta) {
        super.updateState(delta);
        timeToLive -= delta;
        if (timeToLive <= 0) {
            kill();
        }
    }

    public final static class Definition extends StaticAbilityObject.Definition {

        private static final long serialVersionUID = 1000000000L;

        public float damage;
        @Nullable
        private /*final*/ transient ParticleEffect explosionEffect;


        public Definition() {
            super(MODEL_PATH, new CircleCreator(RADIUS), null, CAST_SOUND_PATH);
        }

        public void load(GameContext context) {
            super.load(context);
            Particles.load(context, EXPLOSION_EFFECT_PATH);
        }

        @Override
        public void doneLoading() {
            super.doneLoading();
            explosionEffect = Particles.getOriginal(getContext(), EXPLOSION_EFFECT_PATH);
        }

        @Override
        public GameObject makeInstance(Uid uid, float x, float y) {
            return new ForceBlastObject(this, uid, x, y);
        }
    }
}
