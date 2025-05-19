package com.yousofdevpro.busticketing.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequestDto {
    
    @NotBlank(message = "firstName is required")
    private String firstName;
    
    private String lastName;
    
    @NotBlank(message = "phone is required")
    private String phone;
    
    @NotBlank(message = "email is required")
    @Email(message = "email should be valid")
    private String email;
    
    @NotBlank(message = "password is required")
    @Size(min = 6, message = "password must be at least 6 characters long")
    private String password;
    
    @NotBlank(message = "passwordAgain is required")
    @Size(min = 6, message = "password must be at least 6 characters long")
    private String passwordAgain;
    
    @NotBlank(message = "role is required.")
    @Pattern(regexp = "ADMIN|STAFF|DRIVER|CUSTOMER", message = "role must be one of ADMIN, STAFF, DRIVER, CUSTOMER")
    private String role;
}
