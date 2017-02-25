package ru.spbau.blackout.serializationutils;

import java.io.IOException;

public interface EfficientSerializable {

    String readObjectMethodName = "effectiveReadObject";

    void effectiveWriteObject(EfficientOutputStream out) throws IOException;
    // static void effectiveReadObject(EfficientInputStream in) throws IOException;
}
