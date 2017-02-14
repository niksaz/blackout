package ru.spbau.blackout.graphiceffects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import ru.spbau.blackout.GameContext;
import ru.spbau.blackout.entities.GameObject;

public final class GradualScale implements GraphicEffect {

    private final ModelInstance modelInstance;
    private float time = 0;
    private final float duration;
    private final float scaleFrom;
    private final float scaleTo;

    GradualScale(GameObject gameObject, float scaleFrom, float scaleTo, float duration) {
        modelInstance = gameObject.getModelInstance();
        this.duration = duration;
        this.scaleFrom = scaleFrom;
        this.scaleTo = scaleTo;
        updateScale();
    }

    @Override
    public void remove(GameContext context) {

    }

    @Override
    public void update(float deltaTime) {
        time = Math.min(time + deltaTime, duration);
        updateScale();
    }

    private void updateScale() {
        float scale = scaleFrom + (scaleTo - scaleFrom) * (time / duration);
//        modelInstance.transform.setToScaling(scale, scale, scale);
        // TODO: scale
    }
}
