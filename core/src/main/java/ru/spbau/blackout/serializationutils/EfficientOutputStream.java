package ru.spbau.blackout.serializationutils;

import com.badlogic.gdx.math.Vector2;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class EfficientOutputStream extends OutputStream {

    private final OutputStream out;

    public EfficientOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    public synchronized void writeBoolean(boolean source) throws IOException {
        write(source ? 1 : 0);
    }

    public synchronized void writeByte(byte source) throws IOException {
        write(source);
    }

    public synchronized void writeChar(char source) throws IOException {
        write(ByteBuffer.allocate(Character.SIZE / Byte.SIZE).putChar(source).array());
    }

    public synchronized void writeShort(short source) throws IOException {
        write(ByteBuffer.allocate(Short.SIZE / Byte.SIZE).putShort(source).array());
    }

    public synchronized void writeInt(int source) throws IOException {
        write(ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(source).array());
    }

    public synchronized void writeLong(long source) throws IOException {
        write(ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(source).array());
    }

    public synchronized void writeFloat(float source) throws IOException {
        writeInt(Float.floatToRawIntBits(source));
    }

    public synchronized void writeDouble(double source) throws IOException {
        writeLong(Double.doubleToRawLongBits(source));
    }

    public synchronized void writeString(String source) throws IOException {
        if (source == null) {
            writeInt(-1);
        } else {
            writeInt(source.length());
            write(source.getBytes());
        }
    }

    public synchronized void writeVector2(Vector2 source) throws IOException {
        writeFloat(source.x);
        writeFloat(source.y);
    }

    public synchronized void writeObject(EfficientSerializable source) throws IOException {
        source.effectiveWriteObject(this);
    }
}
