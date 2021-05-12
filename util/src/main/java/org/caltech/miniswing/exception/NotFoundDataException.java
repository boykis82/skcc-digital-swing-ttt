package org.caltech.miniswing.exception;

public class NotFoundDataException extends RuntimeException {
    public NotFoundDataException() {
    }

    public NotFoundDataException(String message) {
        super(message);
    }

    public NotFoundDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundDataException(Throwable cause) {
        super(cause);
    }
}

