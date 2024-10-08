package com.yousofdevpro.busticketing.config.exception;

import org.springframework.http.HttpStatus;

public class InvalidJwtException extends BusinessException{
    public InvalidJwtException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
