package com.postech.reservationservice.dto;

import jakarta.validation.constraints.NotNull;
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
public class NewReservationRequestDto {
    @NotNull
    private UUID restaurantId;
    @NotNull
    private int people;
    @NotNull
    private LocalDate date;
    @NotNull
    private LocalTime time;
}
