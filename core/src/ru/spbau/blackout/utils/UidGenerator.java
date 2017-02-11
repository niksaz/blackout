package ru.spbau.blackout.utils;

public class UidGenerator {

    private int lastUid = 0;

    public Uid next() {
        lastUid += 1;
        return new Uid(lastUid);
    }
}
