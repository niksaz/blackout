package ru.spbau.blackout.java8features;


@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);
}
