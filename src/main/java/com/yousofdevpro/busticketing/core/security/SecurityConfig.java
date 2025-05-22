package com.yousofdevpro.busticketing.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.yousofdevpro.busticketing.core.util.Constants.PERMITTED_URLS;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));
        
        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(PERMITTED_URLS.toArray(new String[0]))
                        .permitAll()
                        .requestMatchers("/api/v1/**").authenticated()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthorizationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        return http.build();
    }
    
    
}
