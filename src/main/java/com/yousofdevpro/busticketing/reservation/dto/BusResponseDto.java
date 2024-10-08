package com.yousofdevpro.busticketing.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class BusResponseDto {
    private Long id;
    private String name;
    private Integer totalSeats;
}
