package ru.spbau.blackout.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface InplaceSerializable {
    /**
     * Opposite to <code>inplaceDeserialize</code>
     */
    void inplaceSerialize(ObjectOutputStream out) throws IOException, ClassNotFoundException;

    /**
     * Opposite to <code>inplaceSerialize</code>
     * Used to do inplace deserialization.
     * Returns any data which is necessary for deserialization of derived classes.
     * (look at <code>GameObject</code> deserialization, to see when the return value is necessary)
     */
    Object inplaceDeserialize(ObjectInputStream in) throws IOException, ClassNotFoundException;
}
