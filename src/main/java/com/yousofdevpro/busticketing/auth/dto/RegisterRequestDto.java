package com.yousofdevpro.busticketing.auth.dto;

import com.yousofdevpro.busticketing.auth.model.Role;
import com.yousofdevpro.busticketing.config.exception.InEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequestDto {
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    private String lastName;
    
    @NotBlank(message = "Phone number is required")
    private String phone;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
    
    @NotBlank(message = "Role is required.")
    @InEnum(value = Role.class, message = "Role must be a value in: CUSTOMER, ADMIN, STAFF, DRIVER")
    private String role;
}
