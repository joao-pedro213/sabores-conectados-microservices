package com.postech.core.reservation.datasource;

import com.postech.core.reservation.dto.ReservationDto;
import reactor.core.publisher.Mono;

public interface IReservationMessageProducer {
    Mono<Void> sendMessage(ReservationDto reservationDto);
}
