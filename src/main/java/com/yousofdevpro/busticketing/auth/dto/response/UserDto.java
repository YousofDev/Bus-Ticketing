package com.yousofdevpro.busticketing.auth.dto.response;

import com.yousofdevpro.busticketing.auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDto {
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Role role;
}
