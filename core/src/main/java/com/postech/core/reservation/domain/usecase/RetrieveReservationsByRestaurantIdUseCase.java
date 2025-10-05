package com.postech.core.reservation.domain.usecase;

import com.postech.core.reservation.domain.entity.ReservationEntity;
import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import com.postech.core.reservation.gateway.ReservationGateway;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.UUID;

@AllArgsConstructor
public class RetrieveReservationsByRestaurantIdUseCase {
    private final ReservationGateway gateway;

    public static RetrieveReservationsByRestaurantIdUseCase build(ReservationGateway gateway) {
        return new RetrieveReservationsByRestaurantIdUseCase(gateway);
    }

    public Flux<ReservationEntity> execute(UUID restaurantId, ReservationStatus status) {
        if (status == null) {
            return this.gateway.findAllByRestaurantId(restaurantId);
        } else {
            return this.gateway.findAllByRestaurantIdAndStatus(restaurantId, status);
        }
    }
}
