package com.yousofdevpro.busticketing.auth.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String role;
}
