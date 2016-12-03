package ru.spbau.blackout.effects;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import ru.spbau.blackout.entities.GameObject;


public class ParticleGameEffect extends GameEffect {
    private ParticleEffect effect;
    private final Matrix4 tmpMatrix = new Matrix4();

    public ParticleGameEffect(ParticleEffect effect) {
        this.effect = effect;
    }


    @Override
    public void update(float deltaTime, GameObject object) {
        tmpMatrix.idt();
        Vector2 pos = object.getPosition();
        tmpMatrix.translate(pos.x, pos.y, object.getHeight());
        effect.setTransform(tmpMatrix);
    }

    @Override
    public void dispose() {
        super.dispose();
        this.effect.dispose();
    }
}
