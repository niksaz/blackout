package ru.spbau.blackout.effects;

import java.io.IOException;

import ru.spbau.blackout.entities.GameObject;
import ru.spbau.blackout.serializationutils.EfficientInputStream;
import ru.spbau.blackout.serializationutils.EfficientOutputStream;
import ru.spbau.blackout.serializationutils.HasState;

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
    public void getState(EfficientOutputStream out) throws IOException {}

    @Override
    public void setState(EfficientInputStream in) throws IOException {}
}
