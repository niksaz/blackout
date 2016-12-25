package ru.spbau.blackout.specialeffects;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.GameContext;


/**
 * Plays the given <code>ParticleEffect</code> once at the given position.
 */
public class ParticleSpecialEffect implements SpecialEffect {

    protected final ParticleEffect effect;
    private final GameContext context;

    protected ParticleSpecialEffect(GameContext context, ParticleEffect effect, float x, float y, float height) {
        this.context = context;
        this.effect = effect;

        // initializeGameWorld
        this.effect.init();
        this.effect.start();
        context.getParticleSystem().add(this.effect);

        // set position
        Matrix4 transform = new Matrix4();
        transform.idt();
        transform.translate(x, y, height);
        this.effect.setTransform(transform);
    }


    /**
     * This constructor will copy the <code>ParticleEffect</code> and then <code>dispose</code> it after usage.
     */
    public static void create(GameContext context, ParticleEffect effect, float x, float y, float height) {
        BlackoutGame.get().specialEffects().add(new ParticleSpecialEffect(context, effect.copy(), x, y, height));
    }

    public static void create(GameContext context, ParticleEffect effect, Vector3 position) {
        create(context, effect, position.x, position.y, position.z);
    }

    @Override
    public boolean update(float deltaTime) {
        if (this.effect.isComplete()) {
            context.getParticleSystem().remove(this.effect);
            this.effect.dispose();
            return false;
        } else {
            return true;
        }
    }
}
