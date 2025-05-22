package com.yousofdevpro.busticketing.core.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Constants {
    
    public static final Set<String> PERMITTED_URLS = new HashSet<>(
            Arrays.asList(
                    "/api/v1/auth/register",
                    "/api/v1/auth/confirm-account",
                    "/api/v1/auth/login",
                    "/api/v1/auth/reset-password",
                    "/api/v1/auth/reset-password-confirm",
                    "/api/v1/auth/send-confirmation-code",
                    "/api/v1/auth/verify-confirmation-code",
                    "/actuator/health"
            ));
}
