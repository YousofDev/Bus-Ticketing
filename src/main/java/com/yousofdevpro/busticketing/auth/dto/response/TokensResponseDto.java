package com.yousofdevpro.busticketing.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class TokensResponseDto {
    private String accessToken;
    private String refreshToken;
}
