package com.postech.core.reservation.domain.usecase;

import com.postech.core.common.exception.EntityNotFoundException;
import com.postech.core.reservation.domain.entity.ReservationEntity;
import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import com.postech.core.reservation.gateway.ReservationGateway;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@AllArgsConstructor
public class ChangeReservationStatusUseCase {
    private final ReservationGateway gateway;

    public static ChangeReservationStatusUseCase build(ReservationGateway gateway) {
        return new ChangeReservationStatusUseCase(gateway);
    }

    public Mono<ReservationEntity> execute(UUID id, ReservationStatus newStatus) {
        return this.gateway
                .findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Reservation")))
                .map(reservationEntity -> reservationEntity.toBuilder().status(newStatus).build())
                .flatMap(this.gateway::save)
                .flatMap(savedReservation -> this.gateway.sendEvent(savedReservation).thenReturn(savedReservation));
    }
}
