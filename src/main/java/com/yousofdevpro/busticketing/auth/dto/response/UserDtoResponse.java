package com.yousofdevpro.busticketing.auth.dto.response;

import com.yousofdevpro.busticketing.auth.model.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
