package com.yousofdevpro.busticketing.core.config;

import com.yousofdevpro.busticketing.auth.model.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class EntityAuditAware implements AuditorAware<Long> {
    
    @NonNull
    @Override
    public Optional<Long> getCurrentAuditor() {
        
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();
        
        if(authentication == null || ! authentication.isAuthenticated() ||
        authentication instanceof AnonymousAuthenticationToken){
            return Optional.empty();
        }
        
        User userPrincipal = (User) authentication.getPrincipal();
        
        return Optional.ofNullable(userPrincipal.getId());
    }
}
