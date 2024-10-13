package com.yousofdevpro.busticketing.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailRequestDto {
    
    @NotBlank(message = "email is required")
    @Email(message = "email should be valid")
    private String email;
}
