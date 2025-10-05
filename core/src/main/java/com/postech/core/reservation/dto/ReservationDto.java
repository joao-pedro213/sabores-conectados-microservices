package com.postech.core.reservation.dto;

import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private UUID id;
    private UUID restaurantId;
    private UUID customerId;
    private ReservationStatus status;
    private int people;
    private LocalDate date;
    private LocalTime time;
    private LocalDateTime createdAt;
}
