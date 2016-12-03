package ru.spbau.blackout.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.BlackoutGame;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.java8features.Optional;


public class ParticleGameEffect extends GameEffect {
    private Optional<ParticleEffect> effect;
    private final Matrix4 tmpMatrix = new Matrix4();

    public ParticleGameEffect(GameObject object, Optional<ParticleEffect> effect) {
        super(object);
        this.effect = effect;
        if (this.effect.isPresent()) {
            ParticleEffect realEffect = this.effect.get();
            realEffect.init();
            BlackoutGame.get().particleSystem().add(realEffect);
        }
        this.updatePosition();
    }

    @Override
    public void update(float deltaTime) {
        this.updatePosition();
    }

    public void updatePosition() {
        if (this.effect.isPresent()) {
            ParticleEffect effect = this.effect.get();
            this.tmpMatrix.idt();
            Vector2 pos = this.object.getPosition();
            this.tmpMatrix.translate(pos.x, pos.y, this.object.getHeight());
            effect.setTransform(this.tmpMatrix);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.effect.isPresent()) {
            ParticleEffect effect = this.effect.get();
            BlackoutGame.get().particleSystem().remove(effect);
            effect.dispose();
        }
    }
}
