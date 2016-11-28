package ru.spbau.blackout.abilities.fireball;

import com.badlogic.gdx.physics.box2d.Shape;

import ru.spbau.blackout.entities.AbilityObject;
import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.utils.Creator;


public class FireballObject extends AbilityObject {
    private float timeRest;


    protected FireballObject(FireballObject.Definition def, float x, float y) {
        super(def, x, y);
        this.timeRest = def.timeToLive;
    }


    @Override
    public void updateState(float deltaTime) {
        super.updateState(deltaTime);
        timeRest -= deltaTime;
        if (timeRest <= 0) {
            this.kill();
        }
    }


    /**
     * Additionally defines timeToLive for an object.
     */
    public static class Definition extends AbilityObject.Definition {
        public float timeToLive;

        public Definition(String modelPath, Creator<Shape> shapeCreator, float mass, float timeToLive) {
            super(modelPath, shapeCreator, mass);
            this.timeToLive = timeToLive;
        }

        @Override
        public GameObject makeInstance(float x, float y) {
            return new FireballObject(this, x, y);
        }
    }
}
