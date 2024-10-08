package com.yousofdevpro.busticketing.config.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yousofdevpro.busticketing.config.exception.AuthenticationException;
import com.yousofdevpro.busticketing.config.exception.ErrorDetails;
import com.yousofdevpro.busticketing.config.exception.InvalidJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain)
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader==null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        
        try{
            String jwt = authHeader.substring(7);
            String username = jwtUtil.extractUsername(jwt);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (username!=null && authentication==null) {
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            
            chain.doFilter(request, response);
            
        } catch (AuthenticationException e) {
            writeException(response, HttpStatus.UNAUTHORIZED,  e);
        } catch (InvalidJwtException e) {
            writeException(response, HttpStatus.FORBIDDEN, e);
        }
        
    }
    
    private void writeException(HttpServletResponse response, HttpStatus status, Exception e) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        ErrorDetails errorDetails = new ErrorDetails(
                e.getMessage(),
                status.value()
        );
        
        ObjectMapper objectMapper = new ObjectMapper();
        
        String jsonResponse = objectMapper.writeValueAsString(errorDetails);
        
        response.getWriter().write(jsonResponse);
    }
    
}
