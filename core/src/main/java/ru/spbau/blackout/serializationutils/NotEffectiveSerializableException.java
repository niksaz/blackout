package ru.spbau.blackout.serializationutils;

public class NotEffectiveSerializableException extends RuntimeException {

    public NotEffectiveSerializableException() {
        super();
    }

    public NotEffectiveSerializableException(String message) {
        super(message);
    }

    public NotEffectiveSerializableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEffectiveSerializableException(Throwable cause) {
        super(cause);
    }

    protected NotEffectiveSerializableException(String message, Throwable cause,
                                                boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
