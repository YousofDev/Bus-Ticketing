package com.yousofdevpro.busticketing.reservation.dto;

import lombok.Getter;

@Getter
public class AddBusRequestDto {
    private String name;
    private String busNumber;
    private Integer totalSeats;
}
