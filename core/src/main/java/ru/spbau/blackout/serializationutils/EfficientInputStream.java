package ru.spbau.blackout.serializationutils;

import com.badlogic.gdx.math.Vector2;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

public class EfficientInputStream extends InputStream {

    private final InputStream in;

    public EfficientInputStream(InputStream in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }

    private byte[] readBytes(int count) throws IOException {
        byte[] buf = new byte[count];
        int offset = 0;
        while (offset < buf.length) {
            int n = read(buf, offset, buf.length);
            if (n == -1) {
                throw new EOFException();
            }
            offset += n;
        }
        return buf;
    }

    public synchronized boolean readBoolean() throws IOException {
        return readByte() == 1;
    }

    public synchronized byte readByte() throws IOException {
        int result = read();
        if (result == -1) {
            throw new EOFException();
        }
        return (byte) result;
    }

    public synchronized char readChar() throws IOException {
        return ByteBuffer.wrap(readBytes(Character.SIZE / Byte.SIZE)).getChar();
    }

    public synchronized short readShort() throws IOException {
        return ByteBuffer.wrap(readBytes(Short.SIZE / Byte.SIZE)).getShort();
    }

    public synchronized float readFloat() throws IOException {
        return ByteBuffer.wrap(readBytes(Float.SIZE / Byte.SIZE)).getFloat();
    }

    public synchronized double readDouble() throws IOException {
        return ByteBuffer.wrap(readBytes(Double.SIZE / Byte.SIZE)).getDouble();
    }

    public synchronized int readInt() throws IOException {
        return ByteBuffer.wrap(readBytes(Integer.SIZE / Byte.SIZE)).getInt();
    }

    public synchronized long readLong() throws IOException {
        return ByteBuffer.wrap(readBytes(Long.SIZE / Byte.SIZE)).getLong();
    }

    public synchronized String readString() throws IOException {
        int size = readInt();
        if (size == -1) {
            return null;
        }
        return new String(readBytes(size));
    }

    public synchronized Vector2 readVector2() throws IOException {
        float x = readFloat();
        float y = readFloat();
        return new Vector2(x, y);
    }

    public synchronized <T extends EfficientSerializable> T readObject(Class<T> cl) throws IOException {
        Method[] methods = cl.getMethods();
        for (Method m : methods) {
            if (!m.getName().equals(EfficientSerializable.readObjectMethodName)) {
                continue;
            }
            try {
                @SuppressWarnings("unchecked")
                T result = (T) m.invoke(null, this);
                return result;
            } catch (IllegalAccessException e) {
                throw new NotEfficientSerializableException();
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof IOException) {
                    throw (IOException) e.getCause();
                } else {
                    throw (RuntimeException) e.getCause();
                }
            }
        }
        throw new NotEfficientSerializableException();
    }
}
