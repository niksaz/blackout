package ru.spbau.blackout.serializationutils;

import java.io.IOException;

public interface EffectiveSerializable {

    String readObjectMethodName = "effectiveReadObject";

    void effectiveWriteObject(EffectiveOutputStream out) throws IOException;
    // static void effectiveReadObject(EffectiveInputStream in) throws IOException;
}
