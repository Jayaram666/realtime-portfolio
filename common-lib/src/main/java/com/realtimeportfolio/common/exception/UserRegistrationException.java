package com.realtimeportfolio.common.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class UserRegistrationException extends ValidationException {

    private final List<String> errors;

    public UserRegistrationException(List<String> errors) {
        super("User registration failed");
        this.errors = errors;
    }
}
