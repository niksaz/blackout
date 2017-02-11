package ru.spbau.blackout.utils;

import java.io.IOException;
import java.io.Serializable;

public class Uid implements Serializable {

    private /*final*/ int uid;

    public Uid(int uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Uid && ((Uid) other).uid == uid;
    }

    @Override
    public int hashCode() {
        return uid;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeInt(uid);
    }
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        uid = in.readInt();
    }
}
