package ru.spbau.blackout.serializationutils;

public class NotEfficientSerializableException extends RuntimeException {

    public NotEfficientSerializableException() {
        super();
    }

    public NotEfficientSerializableException(String message) {
        super(message);
    }

    public NotEfficientSerializableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEfficientSerializableException(Throwable cause) {
        super(cause);
    }

    protected NotEfficientSerializableException(String message, Throwable cause,
                                                boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
