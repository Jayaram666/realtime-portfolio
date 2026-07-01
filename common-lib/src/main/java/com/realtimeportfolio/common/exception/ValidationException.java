package com.realtimeportfolio.common.exception;

import java.util.Collections;
import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<String> validationMessages;

    // Single message constructor
    public ValidationException(String message) {
        super(message);
        this.validationMessages = Collections.singletonList(message); // FIX: Ensure field is never null
    }

    // List of messages constructor
    public ValidationException(List<String> messages) {
        super(messages != null ? String.join(", ", messages) : "Validation failed");
        this.validationMessages = messages != null ? messages : Collections.emptyList(); // FIX: Save the actual list
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.validationMessages = Collections.singletonList(message);
    }

    // Getter so the Exception Handler can read the underlying list
    public List<String> getValidationMessages() {
        return validationMessages;
    }
}
