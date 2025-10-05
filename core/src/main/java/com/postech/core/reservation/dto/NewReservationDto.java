package com.postech.core.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class NewReservationDto {
    private UUID restaurantId;
    private UUID customerId;
    private int people;
    private LocalDate date;
    private LocalTime time;
}
