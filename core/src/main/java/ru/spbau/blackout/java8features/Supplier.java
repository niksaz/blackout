package ru.spbau.blackout.java8features;

@FunctionalInterface
public interface Supplier<T> {
    T get();
}
