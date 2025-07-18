package com.yousofdevpro.busticketing.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequestDto {
    
    @NotBlank(message = "currentPassword is required")
    private String currentPassword;
    
    @NotBlank(message = "password is required")
    @Size(min = 6, message = "password must be at least 6 characters long")
    private String password;
    
    @NotBlank(message = "passwordAgain is required")
    @Size(min = 6, message = "password must be at least 6 characters long")
    private String passwordAgain;
}
