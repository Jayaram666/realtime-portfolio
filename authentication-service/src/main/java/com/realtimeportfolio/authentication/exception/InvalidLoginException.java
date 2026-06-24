package com.realtimeportfolio.authentication.exception;


import com.realtimeportfolio.common.exception.ValidationException;

public class InvalidLoginException extends ValidationException {

    public InvalidLoginException(String message) {
        super(message);
    }
}