package com.yousofdevpro.busticketing.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ProfileRequestDto {
    
    @NotBlank(message = "firstName is required")
    private String firstName;
    
    private String lastName;
    
    @NotBlank(message = "phone is required")
    private String phone;
    
    @NotBlank(message = "email is required")
    @Email(message = "email should be valid")
    private String email;
    
    @NotBlank(message = "role is required.")
    @Pattern(regexp = "ADMIN|STAFF|DRIVER|CUSTOMER", message = "role must be one of ADMIN, STAFF, DRIVER, CUSTOMER")
    private String role;
}
