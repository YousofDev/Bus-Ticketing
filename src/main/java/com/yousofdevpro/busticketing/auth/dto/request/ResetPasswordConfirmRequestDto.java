package com.yousofdevpro.busticketing.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordConfirmRequestDto {
    
    @NotBlank(message = "code is required")
    private String code;
    
    @NotBlank(message = "email is required")
    @Email(message = "email should be valid")
    private String email;
    
    @NotBlank(message = "password is required")
    @Size(min = 6, message = "password must be at least 6 characters long")
    private String password;
    
    @NotBlank(message = "passwordAgain is required")
    @Size(min = 6, message = "password must be at least 6 characters long")
    private String passwordAgain;
}
