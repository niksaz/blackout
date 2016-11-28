package ru.spbau.blackout.java8features;

public class Optional<T> {
    T t;


    private Optional(T t) {
        this.t = t;
    }

    public static <T> Optional<T> of(T t) {
        if (t == null) {
            throw new IllegalArgumentException("Optional.of(null)");
        }
        return new Optional<>(t);
    }

    public static <T>Optional<T> empty() {
        return new Optional<>(null);
    }


    public T get() {
        if (t == null) {
            throw new IllegalStateException("emptyOptional.get()");
        }
        return t;
    }

    public void ifPresent(Consumer<? super T> consumer) {
        if (t != null) {
            consumer.accept(t);
        }
    }

    public boolean isPresent() {
        return t != null;
    }
}
