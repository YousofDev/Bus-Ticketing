package com.yousofdevpro.busticketing.config.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BusinessException{
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
