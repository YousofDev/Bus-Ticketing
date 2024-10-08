package com.yousofdevpro.busticketing.config.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends BusinessException {
    public AuthorizationException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
