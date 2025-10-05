package com.postech.core.reservation.domain.usecase;

import com.postech.core.reservation.domain.entity.ReservationEntity;
import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import com.postech.core.reservation.gateway.ReservationGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetrieveReservationsByRestaurantIdUseCaseTest {

    @Mock
    private ReservationGateway mockReservationGateway;

    @InjectMocks
    private RetrieveReservationsByRestaurantIdUseCase useCase;

    @Test
    @DisplayName("Should retrieve reservations by restaurant id")
    void shouldRetrieveReservationsByRestaurantId() {
        // Given
        final UUID restaurantId = UUID.randomUUID();
        final ReservationEntity reservation1 = ReservationEntity.builder().id(UUID.randomUUID()).restaurantId(restaurantId).build();
        final ReservationEntity reservation2 = ReservationEntity.builder().id(UUID.randomUUID()).restaurantId(restaurantId).build();
        when(this.mockReservationGateway.findAllByRestaurantId(restaurantId)).thenReturn(Flux.just(reservation1, reservation2));

        // When
        final Flux<ReservationEntity> result = this.useCase.execute(restaurantId, null);

        // Then
        StepVerifier.create(result).expectNext(reservation1).expectNext(reservation2).verifyComplete();
    }

    @Test
    @DisplayName("Should retrieve reservations by restaurant id and status")
    void shouldRetrieveReservationsByRestaurantIdAndStatus() {
        // Given
        final UUID restaurantId = UUID.randomUUID();
        final ReservationStatus status = ReservationStatus.PENDING;
        final ReservationEntity reservation1 = ReservationEntity.builder().id(UUID.randomUUID()).restaurantId(restaurantId).status(status).build();
        final ReservationEntity reservation2 = ReservationEntity.builder().id(UUID.randomUUID()).restaurantId(restaurantId).status(status).build();
        when(this.mockReservationGateway.findAllByRestaurantIdAndStatus(restaurantId, status)).thenReturn(Flux.just(reservation1, reservation2));

        // When
        final Flux<ReservationEntity> result = this.useCase.execute(restaurantId, status);

        // Then
        StepVerifier.create(result).expectNext(reservation1).expectNext(reservation2).verifyComplete();
    }

    @Test
    @DisplayName("Should return empty flux when no reservations found")
    void shouldReturnEmptyFluxWhenNoReservationsFound() {
        // Given
        final UUID restaurantId = UUID.randomUUID();
        when(this.mockReservationGateway.findAllByRestaurantId(restaurantId)).thenReturn(Flux.empty());

        // When
        final Flux<ReservationEntity> result = this.useCase.execute(restaurantId, null);

        // Then
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    @DisplayName("Should return empty flux when no reservations found by status")
    void shouldReturnEmptyFluxWhenNoReservationsFoundByStatus() {
        // Given
        final UUID restaurantId = UUID.randomUUID();
        final ReservationStatus status = ReservationStatus.PENDING;
        when(this.mockReservationGateway.findAllByRestaurantIdAndStatus(restaurantId, status)).thenReturn(Flux.empty());

        // When
        final Flux<ReservationEntity> result = this.useCase.execute(restaurantId, status);

        // Then
        StepVerifier.create(result).verifyComplete();
    }
}
