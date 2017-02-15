package ru.spbau.blackout.graphiceffects;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Matrix4;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.GameObject;


/**
 * <code>GraphicEffect</code> which attaches a <code>ParticleEffect</code> to an object.
 */
public final class ParticleGraphicEffect extends GraphicEffect {

    private final ParticleEffect effect;
    private final Matrix4 tmpMatrix = new Matrix4();

    public static void create(GameObject object, ParticleEffect effect, GameContext context) {
        object.addGraphicEffect(new ParticleGraphicEffect(object, effect, context));
    }

    private ParticleGraphicEffect(GameObject gameObject, ParticleEffect effect, GameContext context) {
        super(gameObject);
        this.effect = effect;
        this.effect.init();
        this.effect.start();
        context.getParticleSystem().add(this.effect);
        updatePosition();
    }

    @Override
    public void update(float deltaTime) {
        updatePosition();
    }

    public void updatePosition() {
        tmpMatrix.idt();
        tmpMatrix.translate(gameObject.getChestPivot());
        effect.setTransform(this.tmpMatrix);
    }

    @Override
    public void dispose(GameContext context) {
        super.dispose(context);
        effect.dispose();
        context.getParticleSystem().remove(effect);
    }
}
