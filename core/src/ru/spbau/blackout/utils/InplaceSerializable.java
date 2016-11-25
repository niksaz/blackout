package ru.spbau.blackout.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface InplaceSerializable {
    /**
     * Ignores inplaceDeserializeImpl return value.
     * Don't override this method.
     */
    static void inplaceDeserialize(InplaceSerializable dest, ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        dest.inplaceDeserializeImpl(in);
    }

    static void inplaceSerialize(InplaceSerializable source, ObjectOutputStream out)
            throws IOException, ClassNotFoundException
    {
        source.inpaceSerializeImpl(out);
    }

    void inpaceSerializeImpl(ObjectOutputStream out) throws IOException, ClassNotFoundException;

    /**
     * Used to do inplace deserialization.
     * Returns any data which is necessary for derived classes.
     */
    Object inplaceDeserializeImpl(ObjectInputStream in) throws IOException, ClassNotFoundException;
}
