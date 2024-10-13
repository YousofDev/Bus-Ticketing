package com.yousofdevpro.busticketing.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ConfirmationCodeRequestDto {
    
    @NotBlank(message = "code is required")
    private String code;
    
    @NotBlank(message = "email is required")
    @Email(message = "email should be valid")
    private String email;
}
