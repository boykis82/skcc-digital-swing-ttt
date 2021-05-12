package org.caltech.miniswing.exception;

public class IllegalServiceStatusException extends RuntimeException {
    public IllegalServiceStatusException() {
    }

    public IllegalServiceStatusException(String message) {
        super(message);
    }

    public IllegalServiceStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalServiceStatusException(Throwable cause) {
        super(cause);
    }
}
