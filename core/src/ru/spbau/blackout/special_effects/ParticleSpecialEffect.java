package ru.spbau.blackout.special_effects;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ru.spbau.blackout.BlackoutGame;


/**
 * Plays the given <code>ParticleEffect</code> once at the given position.
 */
public class ParticleSpecialEffect implements SpecialEffect {
    protected final ParticleEffect effect;


    protected ParticleSpecialEffect(ParticleEffect effect, float x, float y, float height) {
        this.effect = effect;

        // initialize
        this.effect.init();
        this.effect.start();
        BlackoutGame.get().particleSystem().add(this.effect);

        // set position
        Matrix4 transform = new Matrix4();
        transform.idt();
        transform.translate(x, y, height);
        this.effect.setTransform(transform);
    }


    /**
     * This constructor will copy the <code>ParticleEffect</code> and then will <code>dispose</code> it after usage.
     */
    public static void createWithCopy(ParticleEffect effect, float x, float y, float height) {
        create(effect.copy(), x, y, height);
    }

    /**
     * This constructor will NOT copy the <code>ParticleEffect</code> but it will <code>dispose</code> it after usage.
     */
    public static void create(ParticleEffect effect, float x, float y, float height) {
        BlackoutGame.get().specialEffects().add(new ParticleSpecialEffect(effect, x, y, height));
    }

    /**
     * Just calls <code>ParticleSpecialEffect.create(effect, position.x, position.y);</code>
     */
    public static void create(ParticleEffect effect, Vector3 position) {
        create(effect, position.x, position.y, position.z);
    }


    @Override
    public boolean update(float deltaTime) {
        if (this.effect.isComplete()) {
            BlackoutGame game = BlackoutGame.get();
            game.particleSystem().remove(this.effect);
            this.effect.dispose();
            return false;
        } else {
            return true;
        }
    }
}
