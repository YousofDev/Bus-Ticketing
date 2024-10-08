package com.yousofdevpro.busticketing.config.exception;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<InEnum, String> {
    
    private Enum<?>[] enumConstants;
    private String message;
    
    @Override
    public void initialize(InEnum annotation) {
        this.enumConstants = annotation.value().getEnumConstants();
        this.message = annotation.message(); // Get custom message from annotation
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value==null || value.isEmpty()) {
            return true; // Handle null or empty separately with @NotBlank if needed
        }
        
        for (Enum<?> enumConstant : enumConstants) {
            if (enumConstant.name().equalsIgnoreCase(value)) {
                return true; // Valid value found
            }
        }
        
        // If invalid, set a custom error message with field name
        context.disableDefaultConstraintViolation(); // Disable default message
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
        
        return false; // No valid value found
    }
}
