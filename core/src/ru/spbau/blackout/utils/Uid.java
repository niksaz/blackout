package ru.spbau.blackout.utils;

import java.io.Serializable;

public class Uid implements Serializable {

    private final int uid;

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
    public String toString() {
        return "#" + uid;
    }
}
