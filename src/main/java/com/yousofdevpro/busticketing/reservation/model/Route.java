package com.yousofdevpro.busticketing.reservation.model;

import com.yousofdevpro.busticketing.core.config.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "route")
@EntityListeners(AuditingEntityListener.class)
public class Route extends BaseEntity{
    
    @Column(nullable = false)
    private String departurePoint;
    
    @Column(nullable = false)
    private String destinationPoint;
    
    @OneToMany(mappedBy = "route")
    private List<Appointment> appointments;
}
