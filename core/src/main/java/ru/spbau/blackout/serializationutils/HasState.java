package ru.spbau.blackout.serializationutils;

import java.io.IOException;

public interface HasState {
    /**
     * Opposite to <code>setState</code>
     */
    void getState(EfficientOutputStream out) throws IOException;

    /**
     * Opposite to <code>getState</code>
     * Used to do inplace deserialization.
     * Returns any data which is necessary for deserialization of derived classes.
     * (look at <code>GameObject</code> deserialization, to see when the return value is necessary)
     */
    void setState(EfficientInputStream in) throws IOException;
}
