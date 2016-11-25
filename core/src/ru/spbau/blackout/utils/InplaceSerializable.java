package ru.spbau.blackout.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface InplaceSerializable {
    /**
     * Ignores inplaceDeserializeImpl return value.
     */
    static void inplaceDeserialize(InplaceSerializable dest, ObjectInputStream in)
        throws IOException, ClassNotFoundException
    {
        dest.inplaceDeserializeImpl(in);
    }

    /**
     * Same as <code>source.inplaceSerializeImpl()</code>, but highly preferred
     * because it looks similar to <code>inplaceDeserialize(dest, in)</code>.
     */
    static void inplaceSerialize(InplaceSerializable source, ObjectOutputStream out)
            throws IOException, ClassNotFoundException
    {
        source.inpaceSerializeImpl(out);
    }

    /**
     * Opposite to <code>inplaceDeserializeImpl</code>
     */
    void inpaceSerializeImpl(ObjectOutputStream out) throws IOException, ClassNotFoundException;

    /**
     * Opposite to <code>inplaceSerializeImpl</code>
     * Used to do inplace deserialization.
     * Returns any data which is necessary for deserialization of derived classes.
     * (look at <code>GameObject</code> deserialization, to see when the return value is necessary)
     */
    Object inplaceDeserializeImpl(ObjectInputStream in) throws IOException, ClassNotFoundException;
}
