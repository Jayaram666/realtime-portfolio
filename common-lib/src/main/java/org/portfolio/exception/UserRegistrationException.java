package org.portfolio.exception;

import java.util.List;

public class UserRegistrationException extends RuntimeException {

    private final List<String> errors;

    public UserRegistrationException(List<String> errors) {
        super("User registration failed");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
