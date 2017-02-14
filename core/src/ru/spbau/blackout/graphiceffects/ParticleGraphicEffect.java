package ru.spbau.blackout.graphiceffects;

import com.badlogic.gdx.graphics.g3d.particles.ParticleController;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Matrix4;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.GameObject;


/**
 * <code>GraphicEffect</code> which attaches a <code>ParticleEffect</code> to an object.
 */
public class ParticleGraphicEffect implements GraphicEffect {

    private final ParticleEffect effect;
    private final Matrix4 tmpMatrix = new Matrix4();
    private final GameObject object;


    public ParticleGraphicEffect(GameContext context, GameObject object, ParticleEffect effect) {
        this.object = object;
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
        tmpMatrix.translate(object.getChestPivot());
        effect.setTransform(this.tmpMatrix);
    }

    @Override
    public void remove(GameContext context) {
        context.getParticleSystem().remove(effect);
        effect.dispose();
    }
}
