package ru.spbau.blackout.graphiceffects;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Matrix4;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.entities.GameObject;


/**
 * <code>GraphicEffect</code> which attaches a <code>ParticleEffect</code> to an object.
 */
public class ParticleGraphicEffect implements GraphicEffect {

    private final ParticleEffect effect;
    private final Matrix4 tmpMatrix = new Matrix4();
    private final GameObject object;


    public ParticleGraphicEffect(GameObject object, ParticleEffect effect) {
        this.object = object;
        this.effect = effect;
        this.effect.init();
        BlackoutGame.get().particleSystem().add(this.effect);
        this.updatePosition();
    }

    @Override
    public void update(float deltaTime) {
        this.updatePosition();
    }

    public void updatePosition() {
        this.tmpMatrix.idt();
        this.tmpMatrix.translate(this.object.getChestPivot());
        this.effect.setTransform(this.tmpMatrix);
    }

    @Override
    public void remove() {
        BlackoutGame.get().particleSystem().remove(this.effect);
        this.effect.dispose();
    }
}
