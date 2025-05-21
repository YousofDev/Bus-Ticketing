package com.yousofdevpro.busticketing.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequestDto {
    
    @NotBlank(message = "email is required")
    @Email(message = "email should be valid")
    private String email;
}
