package com.yousofdevpro.busticketing.core.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorDetails> handleBusinessException(
            BusinessException ex) {
        
        ErrorDetails errorDetails = new ErrorDetails(
                ex.getMessage(),
                ex.getStatus().value()
        );
        
        return new ResponseEntity<>(errorDetails, ex.getStatus());
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetails> handleBadCredentialsException(
            BadCredentialsException ex) {
        
        ErrorDetails errorDetails = new ErrorDetails(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value()
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorDetails> handleInternalAuthenticationException(
            InternalAuthenticationServiceException ex) {
        
        ErrorDetails errorDetails = new ErrorDetails(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED.value()
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDetails> handleNoHandlerFoundException(
            NoHandlerFoundException ex) {
        
        ErrorDetails errorDetails = new ErrorDetails(
                ex.getLocalizedMessage(),
                HttpStatus.NOT_FOUND.value()
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorDetails> handleNoHandlerFoundException(
            NoResourceFoundException ex) {
        
        ErrorDetails errorDetails = new ErrorDetails(
                ex.getLocalizedMessage(),
                HttpStatus.NOT_FOUND.value()
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorDetails> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex) {
        
        ErrorDetails errorDetails = new ErrorDetails(
                ex.getLocalizedMessage(),
                HttpStatus.NOT_FOUND.value()
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetails> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        
        ErrorDetails errorDetails = new ErrorDetails(
                "Validation errors occurred!",
                HttpStatus.BAD_REQUEST.value(),
                errors
        );
        
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
        
        ErrorDetails errorDetails = new ErrorDetails(
                "Something went wrong, try a gain!",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        
        System.out.println("\nGlobalException: " + ex.getMessage());
        
        return new ResponseEntity<>(
                errorDetails, HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    
}
