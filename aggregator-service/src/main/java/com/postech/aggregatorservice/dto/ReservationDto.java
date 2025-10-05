package com.postech.aggregatorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@SchemaMapping("Reservation")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    private UUID id;
    private UUID restaurantId;
    private UUID customerId;
    private String status;
    private int people;
    private LocalDate date;
    private LocalTime time;
    private LocalDateTime createdAt;
}
