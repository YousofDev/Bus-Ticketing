package com.yousofdevpro.busticketing.reservation.model;

import com.yousofdevpro.busticketing.auth.model.User;
import com.yousofdevpro.busticketing.core.config.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment")
@EntityListeners(AuditingEntityListener.class)
public class Appointment extends BaseEntity {
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CalendarDay calendarDay;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceGrade serviceGrade;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    @Column(nullable = false)
    private LocalTime departureTime;
    
    @Column(nullable = false)
    private LocalTime arrivalTime;
    
    @Column(nullable = false)
    private LocalDate effectiveDate;
    
    @Column()
    private LocalDate endDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_user_id", nullable = false)
    private User driver;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;
    
    @OneToMany(mappedBy = "appointment")
    private List<Ticket> tickets;
}
