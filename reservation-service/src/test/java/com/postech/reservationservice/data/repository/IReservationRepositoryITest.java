package com.postech.reservationservice.data.repository;

import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import com.postech.reservationservice.data.document.ReservationDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

@DataMongoTest
class IReservationRepositoryITest extends TestContainerConfig {

    @Autowired
    private IReservationRepository repository;

    @Test
    void shouldFindAllByRestaurantId() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        ReservationDocument reservation1 = ReservationDocument.builder().id(UUID.randomUUID()).restaurantId(restaurantId).build();
        ReservationDocument reservation2 = ReservationDocument.builder().id(UUID.randomUUID()).restaurantId(restaurantId).build();
        this.repository.saveAll(Flux.just(reservation1, reservation2)).blockLast();

        // When
        Flux<ReservationDocument> result = this.repository.findAllByRestaurantId(restaurantId);

        // Then
        StepVerifier
                .create(result)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void shouldFindAllByRestaurantIdAndStatus() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        ReservationStatus status = ReservationStatus.PENDING;
        ReservationDocument reservation1 = ReservationDocument
                .builder()
                .id(UUID.randomUUID())
                .restaurantId(restaurantId)
                .status(status)
                .build();
        ReservationDocument reservation2 = ReservationDocument
                .builder()
                .id(UUID.randomUUID())
                .restaurantId(restaurantId)
                .status(status)
                .build();
        ReservationDocument reservation3 = ReservationDocument
                .builder()
                .id(UUID.randomUUID())
                .restaurantId(restaurantId)
                .status(ReservationStatus.CONFIRMED)
                .build();
        this.repository.saveAll(Flux.just(reservation1, reservation2, reservation3)).blockLast();

        // When
        Flux<ReservationDocument> result = this.repository.findAllByRestaurantIdAndStatus(restaurantId, status);

        // Then
        StepVerifier
                .create(result)
                .expectNextCount(2)
                .verifyComplete();
    }
}
