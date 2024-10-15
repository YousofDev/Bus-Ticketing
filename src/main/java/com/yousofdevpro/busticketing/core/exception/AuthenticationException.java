package com.yousofdevpro.busticketing.core.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BusinessException {
    public AuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
