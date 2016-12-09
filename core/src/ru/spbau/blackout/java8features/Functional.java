package ru.spbau.blackout.java8features;

public final class Functional {
    private Functional() {}

    // name forEach doesn'value work properly
    public static <T, C extends Iterable<T>> void foreach(C c, Consumer<T> consumer) {
        for (T t : c) {
            consumer.accept(t);
        }
    }
}
