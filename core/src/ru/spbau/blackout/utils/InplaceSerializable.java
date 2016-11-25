package ru.spbau.blackout.utils;

import java.io.IOException;
import java.io.Serializable;

public interface InplaceSerializable extends Serializable {
    void inplaceDeserialize(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException;
}
