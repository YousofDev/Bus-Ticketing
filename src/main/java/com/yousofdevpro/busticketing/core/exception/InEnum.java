package com.yousofdevpro.busticketing.core.exception;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InEnum {
    Class<? extends Enum<?>> value(); // The enum class to validate against
    
    String message() default "Invalid value for enum"; // Default error message
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
