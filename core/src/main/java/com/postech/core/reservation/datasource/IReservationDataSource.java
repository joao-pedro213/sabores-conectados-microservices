package com.postech.core.reservation.datasource;

import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import com.postech.core.reservation.dto.ReservationDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IReservationDataSource {
    Mono<ReservationDto> save(ReservationDto reservationDto);

    Mono<ReservationDto> findById(UUID id);

    Flux<ReservationDto> findAllByRestaurantId(UUID restaurantId);

    Flux<ReservationDto> findAllByRestaurantIdAndStatus(UUID restaurantId, ReservationStatus status);
}
