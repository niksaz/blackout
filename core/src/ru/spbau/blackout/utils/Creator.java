package ru.spbau.blackout.utils;

import java.io.Serializable;

/**
 * Used to send information about how to create object via network.
 * Useful for either objects which are not Serializable or very big
 * objects which can be easily created locally.
 */
public interface Creator<T> extends Serializable {
    T create();
}
