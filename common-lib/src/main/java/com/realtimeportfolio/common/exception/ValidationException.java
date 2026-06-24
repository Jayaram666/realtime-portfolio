package com.realtimeportfolio.common.exception;

import java.util.List;

public class ValidationException extends RuntimeException {

    private List<String> validationMessages;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(List<String> messages) {
        super(String.join(", ", messages));
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
