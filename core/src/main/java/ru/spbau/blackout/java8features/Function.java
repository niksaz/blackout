package ru.spbau.blackout.java8features;


@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}
