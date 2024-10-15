package com.yousofdevpro.busticketing.auth.model;

import com.yousofdevpro.busticketing.reservation.model.Appointment;
import com.yousofdevpro.busticketing.reservation.model.Ticket;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = true)
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String phone;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = true)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @OneToMany(mappedBy = "driver")
    private List<Appointment> appointments;
    
    @OneToMany(mappedBy = "customer")
    private List<Ticket> tickets;
    
    @Column(nullable = true)
    private String otpCode;
    
    @Column(nullable = true)
    private LocalDateTime otpCodeExpiresAt;
    
    @Column(nullable = false)
    private Boolean isConfirmed;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;
    
    @CreatedBy
    private Long createdBy;
    
    @LastModifiedBy
    @Column(insertable = false)
    private Long updatedBy;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
    
    @Override
    public String getUsername() {
        return email;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return isConfirmed;
    }
    
    public boolean isOtpNotValid(String code) {
        // Check if otpCode has been used (set to null)
        if (this.getOtpCode()==null && this.getOtpCodeExpiresAt()==null) {
            return true; // OTP not valid as it has already been used
        }
        
        // Check if otpCode is null or expired or does not match
        return this.getOtpCode()==null ||
                this.getOtpCodeExpiresAt().isBefore(LocalDateTime.now()) ||
                !this.getOtpCode().equals(code);
    }
    
}
