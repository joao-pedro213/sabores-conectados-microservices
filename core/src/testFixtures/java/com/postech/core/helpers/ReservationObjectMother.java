package com.postech.core.helpers;

import com.postech.core.reservation.domain.entity.ReservationEntity;
import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import com.postech.core.reservation.dto.NewReservationDto;
import com.postech.core.reservation.dto.ReservationDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

public class ReservationObjectMother {

    public static ReservationEntity buildReservationEntity(Map<String, Object> data) {
        return ReservationEntity.builder()
                .id((UUID) data.get("id"))
                .restaurantId((UUID) data.get("restaurantId"))
                .customerId((UUID) data.get("customerId"))
                .status(ReservationStatus.valueOf((String) data.get("status")))
                .people((Integer) data.get("people"))
                .date((LocalDate) data.get("date"))
                .time((LocalTime) data.get("time"))
                .createdAt(data.get("createdAt") == null ? LocalDateTime.now() : LocalDateTime.parse(data.get("createdAt").toString()))
                .build();
    }

    public static ReservationDto buildReservationDto(Map<String, Object> data) {
        return ReservationDto.builder()
                .id((UUID) data.get("id"))
                .restaurantId((UUID) data.get("restaurantId"))
                .customerId((UUID) data.get("customerId"))
                .status(ReservationStatus.valueOf((String) data.get("status")))
                .people((Integer) data.get("people"))
                .date((LocalDate) data.get("date"))
                .time((LocalTime) data.get("time"))
                .createdAt(data.get("createdAt") == null ? LocalDateTime.now() : LocalDateTime.parse(data.get("createdAt").toString()))
                .build();
    }

    public static NewReservationDto buildNewReservationDto(Map<String, Object> data) {
        return NewReservationDto
                .builder()
                .restaurantId((UUID) data.get("restaurantId"))
                .customerId((UUID) data.get("customerId"))
                .people((Integer) data.get("people"))
                .date((LocalDate) data.get("date"))
                .time((LocalTime) data.get("time"))
                .build();
    }
}
