package ru.spbau.blackout.graphic_effects;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.java8features.Optional;


/**
 * <code>GraphicEffect</code> which attaches a <code>ParticleEffect</code> to an object.
 */
public class ParticleGraphicEffect extends GraphicEffect {
    private ParticleEffect effect;
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
        Vector2 pos = this.object.getPosition();
        this.tmpMatrix.translate(pos.x, pos.y, this.object.getPivotHeight());
        this.effect.setTransform(this.tmpMatrix);
    }

    @Override
    public void dispose() {
        super.dispose();
        BlackoutGame.get().particleSystem().remove(this.effect);
        this.effect.dispose();
    }
}
