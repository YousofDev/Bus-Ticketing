package com.yousofdevpro.busticketing.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequestDto {
    
    @NotBlank(message = "email is required")
    @Email(message = "email should be valid")
    private String email;
    
    @NotBlank(message = "password is required")
    private String password;
}
