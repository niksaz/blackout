package ru.spbau.blackout.effects;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.utils.HasState;

public abstract class PhysicEffect extends Effect implements HasState {

    public PhysicEffect(GameObject gameObject) {
        super(gameObject);
        gameObject.getPhysicEffects().add(this);
    }

    @Override
    public void remove() {
        gameObject.getPhysicEffects().remove(this);
        dispose();
    }

    @Override
    public void getState(ObjectOutputStream out) throws IOException, ClassNotFoundException {}

    @Override
    public void setState(ObjectInputStream in) throws IOException, ClassNotFoundException {}
}
