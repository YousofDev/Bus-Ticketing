package com.yousofdevpro.busticketing.config.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorDetails {
    private String message;
    private int status;
    private List<String> errors;
    
    public ErrorDetails(String message, int status) {
        this.message = message;
        this.status = status;
    }
    
    public ErrorDetails(String message, int status, List<String> errors) {
        this.message = message;
        this.status = status;
        this.errors = errors;
    }
}
