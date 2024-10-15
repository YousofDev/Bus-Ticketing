package com.yousofdevpro.busticketing.core.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AppConfig {
    
    @Bean
    public AuditorAware<Long> auditorAware() {
        return new AppAuditAware();
    }
    
}
