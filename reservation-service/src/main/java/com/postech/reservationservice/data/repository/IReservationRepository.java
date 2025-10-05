package com.postech.reservationservice.data.repository;

import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import com.postech.reservationservice.data.document.ReservationDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface IReservationRepository extends ReactiveMongoRepository<ReservationDocument, UUID> {
    Flux<ReservationDocument> findAllByRestaurantId(UUID restaurantId);

    Flux<ReservationDocument> findAllByRestaurantIdAndStatus(UUID restaurantId, ReservationStatus status);
}
