package ru.spbau.blackout.abilities.fireball;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.effects.ParticleGameEffect;
import ru.spbau.blackout.entities.AbilityObject;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.entities.GameUnit;
import ru.spbau.blackout.java8features.Optional;
import ru.spbau.blackout.utils.Creator;
import ru.spbau.blackout.utils.Particles;

import static ru.spbau.blackout.abilities.fireball.FireballAbility.IMPULSE_FACTOR;


public class FireballObject extends AbilityObject {
    private float timeRest;


    protected FireballObject(FireballObject.Definition def, float x, float y) {
        super(def, x, y);
        this.timeRest = def.timeToLive;

        ParticleGameEffect effect = new ParticleGameEffect(this, def.particleEffect.map(ParticleEffect::copy));
        this.effects.add(effect);
    }


    @Override
    public void beginContact(GameObject object) {
        super.beginContact(object);
        if (object instanceof GameUnit) {
            GameUnit unit = (GameUnit) object;
            unit.applyImpulse(this.velocity.x * IMPULSE_FACTOR, this.velocity.y * IMPULSE_FACTOR);
        }
        this.kill();
    }

    @Override
    public void updateState(float deltaTime) {
        super.updateState(deltaTime);
        timeRest -= deltaTime;
        if (timeRest <= 0) {
            this.kill();
        }
    }


    /**
     * Additionally defines timeToLive for an object.
     */
    public static class Definition extends AbilityObject.Definition {
        public static final String PARTICLES_PATH = "abilities/fireball/particles/fireball.pfx";


        public float timeToLive;
        private /*final*/ Optional<ParticleEffect> particleEffect;


        public Definition(String modelPath, Creator<Shape> shapeCreator, float mass, float timeToLive) {
            super(modelPath, shapeCreator, mass);
            this.timeToLive = timeToLive;
        }

        @Override
        public void load(GameContext context) {
            super.load(context);
            Particles.load(PARTICLES_PATH, context);
        }

        @Override
        public void doneLoading(GameContext context) {
            super.doneLoading(context);
            this.particleEffect = Particles.get(PARTICLES_PATH, context);
        }

        @Override
        public GameObject makeInstance(float x, float y) {
            return new FireballObject(this, x, y);
        }
    }
}
