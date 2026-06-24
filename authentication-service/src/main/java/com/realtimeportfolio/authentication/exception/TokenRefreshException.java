package com.realtimeportfolio.authentication.exception;

import com.realtimeportfolio.common.exception.ValidationException;

public class TokenRefreshException extends ValidationException {

    public TokenRefreshException(String message) {
        super(message);
    }
}
