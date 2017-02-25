package ru.spbau.blackout.utils;

import java.io.IOException;
import java.io.Serializable;

import ru.spbau.blackout.serializationutils.EfficientInputStream;
import ru.spbau.blackout.serializationutils.EfficientOutputStream;
import ru.spbau.blackout.serializationutils.EfficientSerializable;

public final class Uid implements EfficientSerializable, Serializable {

    private static final int CACHE_SIZE = 256;

    private final int uid;
    private static Uid[] CACHE = new Uid[CACHE_SIZE];

    static {
        for (int i = 0; i < CACHE_SIZE; i++) {
            CACHE[i] = new Uid(i);
        }
    }

    public static Uid get(int uid) {
        return 0 <= uid && uid < CACHE_SIZE ? CACHE[uid] : new Uid(uid);
    }

    private Uid(int uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object other) {
        return other != null && other instanceof Uid && ((Uid) other).uid == uid;
    }

    @Override
    public int hashCode() {
        return uid;
    }

    @Override
    public void effectiveWriteObject(EfficientOutputStream out) throws IOException {
        out.writeInt(uid);
    }

    public static Uid effectiveReadObject(EfficientInputStream in) throws IOException {
        return new Uid(in.readInt());
    }
}
