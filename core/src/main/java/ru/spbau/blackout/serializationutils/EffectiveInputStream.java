package ru.spbau.blackout.serializationutils;

import com.badlogic.gdx.math.Vector2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

public class EffectiveInputStream {

    private final InputStream in;

    public EffectiveInputStream(InputStream in) {
        this.in = in;
    }


    public synchronized boolean readBoolean() throws IOException {
        return in.read() == 1;
    }

    public synchronized byte readByte() throws IOException {
        return (byte) in.read();
    }

    public synchronized char readChar() throws IOException {
        final int size = Character.SIZE / Byte.SIZE;
        byte[] bytes = new byte[size];
        if (in.read(bytes) != size) {
            throw new IOException();
        }
        return ByteBuffer.wrap(bytes).getChar();
    }

    public synchronized short readShort() throws IOException {
        final int size = Short.SIZE / Byte.SIZE;
        byte[] bytes = new byte[size];
        if (in.read(bytes) != size) {
            throw new IOException();
        }
        return ByteBuffer.wrap(bytes).getShort();
    }

    public synchronized float readFloat() throws IOException {
        final int size = Float.SIZE / Byte.SIZE;
        byte[] bytes = new byte[size];
        if (in.read(bytes) != size) {
            throw new IOException();
        }
        return ByteBuffer.wrap(bytes).getFloat();
    }

    public synchronized double readDouble() throws IOException {
        final int size = Double.SIZE / Byte.SIZE;
        byte[] bytes = new byte[size];
        if (in.read(bytes) != size) {
            throw new IOException();
        }
        return ByteBuffer.wrap(bytes).getDouble();
    }

    public synchronized int readInt() throws IOException {
        final int size = Integer.SIZE / Byte.SIZE;
        byte[] bytes = new byte[size];
        if (in.read(bytes) != size) {
            throw new IOException();
        }
        return ByteBuffer.wrap(bytes).getInt();
    }

    public synchronized long readLong() throws IOException {
        final int size = Long.SIZE / Byte.SIZE;
        byte[] bytes = new byte[size];
        if (in.read(bytes) != size) {
            throw new IOException();
        }
        return ByteBuffer.wrap(bytes).getLong();
    }

    public synchronized String readString() throws IOException {
        boolean isPresent = readBoolean();
        if (isPresent) {
            int size = readInt();
            byte[] bytes = new byte[size];
            if (in.read(bytes) != size) {
                throw new IOException();
            }
            return new String(bytes);
        } else {
            return null;
        }
    }

    public synchronized Vector2 readVector2() throws IOException {
        float x = readFloat();
        float y = readFloat();
        return new Vector2(x, y);
    }

    public synchronized <T> T readObject(Class<T> cl) throws IOException {
        Method[] methods = cl.getMethods();
        for (Method m : methods) {
            if (m.getName().equals(EffectiveSerializable.readObjectMethodName)) {
                try {
                    @SuppressWarnings("unchecked")
                    T result = (T) m.invoke(null, this);
                    return result;
                } catch (IllegalAccessException e) {
                    throw new NotEffectiveSerializableException();
                } catch (InvocationTargetException e) {
                    if (e.getCause() instanceof IOException) {
                        throw (IOException) e.getCause();
                    } else {
                        throw (RuntimeException) e.getCause();
                    }
                }
            }
        }
        throw new NotEffectiveSerializableException();
    }
}
