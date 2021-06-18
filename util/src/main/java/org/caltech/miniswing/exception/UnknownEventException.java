package org.caltech.miniswing.exception;

public class UnknownEventException extends RuntimeException {
    public UnknownEventException() {
    }

    public UnknownEventException(String message) {
        super(message);
    }

    public UnknownEventException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownEventException(Throwable cause) {
        super(cause);
    }
}

