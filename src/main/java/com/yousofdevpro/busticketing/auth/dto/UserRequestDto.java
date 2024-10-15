package com.yousofdevpro.busticketing.auth.dto;

import com.yousofdevpro.busticketing.auth.model.Role;
import com.yousofdevpro.busticketing.core.exception.InEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserRequestDto {
    
    @NotBlank(message = "firstName is required")
    private String firstName;
    
    private String lastName;
    
    @NotBlank(message = "phone is required")
    private String phone;
    
    @NotBlank(message = "email is required")
    @Email(message = "email should be valid")
    private String email;
    
    @NotBlank(message = "role is required.")
    @InEnum(value = Role.class, message = "role must be a value in: CUSTOMER, ADMIN, STAFF, DRIVER")
    private String role;
}
