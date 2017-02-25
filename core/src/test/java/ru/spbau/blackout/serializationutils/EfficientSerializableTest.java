package ru.spbau.blackout.serializationutils;

import com.badlogic.gdx.math.Vector2;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Random;

import ru.spbau.blackout.utils.Utils;

import static org.junit.Assert.*;

public class EfficientSerializableTest {

    @Test
    public void serializeBooleanTest() throws Exception {
        final int n = 10;
        final Random random = new Random();
        final boolean[] arr = new boolean[n];
        byte[] encoded;

        try (ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
             EfficientOutputStream os = new EfficientOutputStream(bytesOS)) {
            for (int i = 0; i < n; i++) {
                arr[i] = random.nextBoolean();
                os.writeBoolean(arr[i]);
            }
            encoded = bytesOS.toByteArray();
        }

        try (InputStream bytesIS = new ByteArrayInputStream(encoded);
             EfficientInputStream is = new EfficientInputStream(bytesIS)) {
            for (int i = 0; i < n; i++) {
                assertEquals(arr[i], is.readBoolean());
            }
        }
    }


    @Test
    public void serializeByteTest() throws Exception {
        final int n = 10;
        final Random random = new Random();
        final byte[] arr = new byte[n];
        byte[] encoded;

        try (ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
             EfficientOutputStream os = new EfficientOutputStream(bytesOS)) {
            for (int i = 0; i < n; i++) {
                arr[i] = (byte) random.nextInt();
                os.writeByte(arr[i]);
            }
            encoded = bytesOS.toByteArray();
        }

        try (InputStream bytesIS = new ByteArrayInputStream(encoded);
             EfficientInputStream is = new EfficientInputStream(bytesIS)) {
            for (int i = 0; i < n; i++) {
                assertEquals(arr[i], is.readByte());
            }
        }
    }


    @Test
    public void serializeCharTest() throws Exception {
        final int n = 10;
        final Random random = new Random();
        final char[] arr = new char[n];
        byte[] encoded;

        try (ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
             EfficientOutputStream os = new EfficientOutputStream(bytesOS)) {
            for (int i = 0; i < n; i++) {
                arr[i] = (char) random.nextInt();
                os.writeChar(arr[i]);
            }
            encoded = bytesOS.toByteArray();
        }

        try (InputStream bytesIS = new ByteArrayInputStream(encoded);
             EfficientInputStream is = new EfficientInputStream(bytesIS)) {
            for (int i = 0; i < n; i++) {
                assertEquals(arr[i], is.readChar());
            }
        }
    }


    @Test
    public void serializeShortTest() throws Exception {
        final int n = 10;
        final Random random = new Random();
        final short[] arr = new short[n];
        byte[] encoded;

        try (ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
             EfficientOutputStream os = new EfficientOutputStream(bytesOS)) {
            for (int i = 0; i < n; i++) {
                arr[i] = (short) random.nextInt();
                os.writeShort(arr[i]);
            }
            encoded = bytesOS.toByteArray();
        }

        try (InputStream bytesIS = new ByteArrayInputStream(encoded);
             EfficientInputStream is = new EfficientInputStream(bytesIS)) {
            for (int i = 0; i < n; i++) {
                assertEquals(arr[i], is.readShort());
            }
        }
    }


    @Test
    public void serializeIntTest() throws Exception {
        final int n = 10;
        final Random random = new Random();
        final int[] arr = new int[n];
        byte[] encoded;

        try (ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
             EfficientOutputStream os = new EfficientOutputStream(bytesOS)) {
            for (int i = 0; i < n; i++) {
                arr[i] = random.nextInt();
                os.writeInt(arr[i]);
            }
            encoded = bytesOS.toByteArray();
        }

        try (InputStream bytesIS = new ByteArrayInputStream(encoded);
             EfficientInputStream is = new EfficientInputStream(bytesIS)) {
            for (int i = 0; i < n; i++) {
                assertEquals(arr[i], is.readInt());
            }
        }
    }


    @Test
    public void serializeLongTest() throws Exception {
        final int n = 10;
        final Random random = new Random();
        final long[] arr = new long[n];
        byte[] encoded;

        try (ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
             EfficientOutputStream os = new EfficientOutputStream(bytesOS)) {
            for (int i = 0; i < n; i++) {
                arr[i] = random.nextLong();
                os.writeLong(arr[i]);
            }
            encoded = bytesOS.toByteArray();
        }

        try (InputStream bytesIS = new ByteArrayInputStream(encoded);
             EfficientInputStream is = new EfficientInputStream(bytesIS)) {
            for (int i = 0; i < n; i++) {
                assertEquals(arr[i], is.readLong());
            }
        }
    }


    @Test
    public void serializeFloatTest() throws Exception {
        final int n = 10;
        final Random random = new Random();
        final float[] arr = new float[n];
        byte[] encoded;

        try (ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
             EfficientOutputStream os = new EfficientOutputStream(bytesOS)) {
            for (int i = 0; i < n; i++) {
                arr[i] = random.nextFloat();
                os.writeFloat(arr[i]);
            }
            encoded = bytesOS.toByteArray();
        }

        try (InputStream bytesIS = new ByteArrayInputStream(encoded);
             EfficientInputStream is = new EfficientInputStream(bytesIS)) {
            for (int i = 0; i < n; i++) {
                Utils.floatEq(arr[i], is.readFloat());
            }
        }
    }


    @Test
    public void serializeDoubleTest() throws Exception {
        final int n = 10;
        final Random random = new Random();
        final double[] arr = new double[n];
        byte[] encoded;

        try (ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
             EfficientOutputStream os = new EfficientOutputStream(bytesOS)) {
            for (int i = 0; i < n; i++) {
                arr[i] = random.nextDouble();
                os.writeDouble(arr[i]);
            }
            encoded = bytesOS.toByteArray();
        }

        try (InputStream bytesIS = new ByteArrayInputStream(encoded);
             EfficientInputStream is = new EfficientInputStream(bytesIS)) {
            for (int i = 0; i < n; i++) {
                Utils.floatEq(arr[i], is.readDouble());
            }
        }
    }


    @Test
    public void serializeVector2Test() throws Exception {
        final int n = 10;
        final Random random = new Random();
        final Vector2[] arr = new Vector2[n];
        byte[] encoded;

        try (ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
             EfficientOutputStream os = new EfficientOutputStream(bytesOS)) {
            for (int i = 0; i < n; i++) {
                arr[i] = new Vector2(random.nextFloat(), random.nextFloat());
                os.writeVector2(arr[i]);
            }
            encoded = bytesOS.toByteArray();
        }

        try (InputStream bytesIS = new ByteArrayInputStream(encoded);
             EfficientInputStream is = new EfficientInputStream(bytesIS)) {
            for (int i = 0; i < n; i++) {
                assertEquals(arr[i], is.readVector2());
            }
        }
    }

    @Test
    public void serializeStringTest() throws Exception {
        RandomStringGenerator stringGenerator = new RandomStringGenerator();

        final String[] arr = {
                null,
                "",
                "hello",
                stringGenerator.nextString(10),
                stringGenerator.nextString(1024),
                stringGenerator.nextString(2048)
        };
        byte[] encoded;

        try (ByteArrayOutputStream bytesOS = new ByteArrayOutputStream();
             EfficientOutputStream os = new EfficientOutputStream(bytesOS)) {
            for (String s: arr) {
                os.writeString(s);
            }
            encoded = bytesOS.toByteArray();
        }

        try (InputStream bytesIS = new ByteArrayInputStream(encoded);
             EfficientInputStream is = new EfficientInputStream(bytesIS)) {
            for (String s: arr) {
                assertEquals(s, is.readString());
            }
        }
    }

    private static class RandomStringGenerator {

        private static final char[] alphabet;

        static {
            StringBuilder builder = new StringBuilder();
            for (char ch = '0'; ch <= '9'; ch++) {
                builder.append(ch);
            }
            for (char ch = 'a'; ch <= 'z'; ch++) {
                builder.append(ch);
            }
            alphabet = builder.toString().toCharArray();
        }

        private final Random random = new Random();

        public String nextString(int length) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(alphabet[random.nextInt(alphabet.length)]);
            }
            return builder.toString();
        }
    }
}
