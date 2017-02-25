package ru.spbau.blackout.serializationutils;

import java.io.IOException;

public interface EfficientSerializable {

    String readObjectMethodName = "effectiveReadObject";

    void effectiveWriteObject(EfficientOutputStream out) throws IOException;
    // public static Object effectiveReadObject(EfficientInputStream in) throws IOException;
}
