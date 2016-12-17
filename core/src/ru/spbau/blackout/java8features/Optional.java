package ru.spbau.blackout.java8features;

import java.io.Serializable;

public final class Optional<T> /*implements Serializable */ {
    T value;


    private Optional(T value) {
        this.value = value;
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

    public static <T> Optional<T> ofNullable(T value) {
        return new Optional<>(value);
    }


    public boolean isPresent() {
        return value != null;
    }

    public T get() {
        if (!this.isPresent()) {
            throw new IllegalStateException("emptyOptional.getOriginal()");
        }
        return value;
    }

    public void ifPresent(Consumer<? super T> consumer) {
        if (this.isPresent()) {
            consumer.accept(value);
        }
    }

    public <U> Optional<U> map(Function<? super T,? extends U> mapper) {
        if (this.isPresent()) {
            return Optional.of(mapper.apply(this.value));
        } else {
            return Optional.empty();
        }
    }
}
