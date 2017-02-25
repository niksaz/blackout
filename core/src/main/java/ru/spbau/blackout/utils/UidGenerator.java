package ru.spbau.blackout.utils;

public class UidGenerator {

    private int lastUid = 0;

    public synchronized Uid next() {
        lastUid += 1;
        return Uid.get(lastUid);
    }
}
