package ru.spbau.blackout.serializationutils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface HasState {
    /**
     * Opposite to <code>setState</code>
     */
    void getState(EffectiveOutputStream out) throws IOException;

    /**
     * Opposite to <code>getState</code>
     * Used to do inplace deserialization.
     * Returns any data which is necessary for deserialization of derived classes.
     * (look at <code>GameObject</code> deserialization, to see when the return value is necessary)
     */
    void setState(EffectiveInputStream in) throws IOException;
}
