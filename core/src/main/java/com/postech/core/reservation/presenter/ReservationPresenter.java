package com.postech.core.reservation.presenter;

import com.postech.core.reservation.domain.entity.ReservationEntity;
import com.postech.core.reservation.dto.ReservationDto;

public class ReservationPresenter {
    public static ReservationPresenter build() {
        return new ReservationPresenter();
    }

    public ReservationDto toDto(ReservationEntity reservationEntity) {
        return ReservationDto
                .builder()
                .id(reservationEntity.getId())
                .restaurantId(reservationEntity.getRestaurantId())
                .customerId(reservationEntity.getCustomerId())
                .status(reservationEntity.getStatus())
                .people(reservationEntity.getPeople())
                .date(reservationEntity.getDate())
                .time(reservationEntity.getTime())
                .createdAt(reservationEntity.getCreatedAt())
                .build();
    }
}
