package ru.spbau.blackout.effects;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.utils.Utils;

public final class GradualScaleEffect extends GraphicEffect {

    private float time = 0;
    private final float duration;
    private final float scaleFrom;
    private final float scaleTo;
    // to avoid redundant allocations
    private final Vector3 tmpScale = new Vector3();

    public GradualScaleEffect(GameObject gameObject, float scaleFrom, float scaleTo, float duration) {
        super(gameObject);
        this.duration = duration;
        // it can't be zero due to some Matrix4 limitations
        this.scaleFrom = Math.max(scaleFrom, 0.001f);
        this.scaleTo = scaleTo;
        updateScale();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (!Utils.floatEq(time, duration)) {
            time = Math.min(time + deltaTime, duration);
            updateScale();
        }
    }

    private void updateScale() {
        ModelInstance modelInstance = gameObject.getModelInstance();
        if (modelInstance != null) {
            float newScale = scaleFrom + (scaleTo - scaleFrom) * (time / duration);
            modelInstance.transform.getScale(tmpScale);
            modelInstance.transform.scale(
                    newScale / tmpScale.x,
                    newScale / tmpScale.y,
                    newScale / tmpScale.z
            );
        }
    }
}
