package ru.spbau.blackout.utils;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Uid implements Externalizable {

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

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(uid);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        uid = in.readInt();
    }
}
