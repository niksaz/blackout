package ru.spbau.blackout.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {

    public static byte[] serializeToByteArray(Object object) {
        final byte[] result;
        try (ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(byteOutputStream)
        ) {
            out.writeObject(object);
            out.flush();
            result = byteOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
        return result;
    }

    public static Object deserializeFromByteArray(byte[] byteRepresentation) {
        final Object object;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteRepresentation);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)
        ) {
            object = objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
        return object;
    }
}
