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
@Table(name = "bus")
@EntityListeners(AuditingEntityListener.class)
public class Bus extends BaseEntity{
    
    @Column(nullable = false, unique = true)
    private String busNumber;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Integer totalSeats;
    
    @OneToMany(mappedBy = "bus")
    private List<Appointment> appointments;
}
