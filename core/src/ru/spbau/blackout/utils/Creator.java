package ru.spbau.blackout.utils;

import java.io.Serializable;

public interface Creator<T> extends Serializable {
    T create();
}
