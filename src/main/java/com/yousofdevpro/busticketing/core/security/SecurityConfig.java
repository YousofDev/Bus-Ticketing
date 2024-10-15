package com.yousofdevpro.busticketing.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));
        
        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers(
                        "/api/v1/auth/register",
                        "/api/v1/auth/confirm-account",
                        "/api/v1/auth/login",
                        "/api/v1/auth/reset-password",
                        "/api/v1/auth/reset-password-confirm",
                        "/api/v1/auth/send-confirmation-code",
                        "/api/v1/auth/verify-confirmation-code"
                )
                .permitAll()
                .requestMatchers("/api/v1/**").authenticated()
                .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(authenticationManager, jwtUtil),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthorizationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(c->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        return http.build();
    }
    
    
    
}
