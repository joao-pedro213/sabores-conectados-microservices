package com.postech.core.reservation.domain.usecase;

import com.postech.core.common.exception.EntityNotFoundException;
import com.postech.core.reservation.domain.entity.ReservationEntity;
import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import com.postech.core.reservation.gateway.ReservationGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeReservationStatusUseCaseTest {

    @Mock
    private ReservationGateway mockGateway;

    @InjectMocks
    private ChangeReservationStatusUseCase useCase;

    @Test
    @DisplayName("Should change reservation status")
    void shouldChangeReservationStatus() {
        // Given
        final UUID id = UUID.randomUUID();
        final ReservationStatus newStatus = ReservationStatus.CONFIRMED;
        final ReservationEntity existingReservation = ReservationEntity.builder().id(id).status(ReservationStatus.PENDING).build();
        final ReservationEntity updatedReservation = existingReservation.toBuilder().status(newStatus).build();
        when(this.mockGateway.findById(id)).thenReturn(Mono.just(existingReservation));
        when(this.mockGateway.save(any(ReservationEntity.class))).thenReturn(Mono.just(updatedReservation));
        when(this.mockGateway.sendEvent(any(ReservationEntity.class))).thenReturn(Mono.empty());

        // When
        final Mono<ReservationEntity> result = this.useCase.execute(id, newStatus);

        // Then
        StepVerifier
                .create(result)
                .expectNextMatches(reservationEntity -> reservationEntity.equals(updatedReservation))
                .verifyComplete();
        ArgumentCaptor<ReservationEntity> captor = ArgumentCaptor.forClass(ReservationEntity.class);
        verify(this.mockGateway).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isNotNull().isEqualTo(newStatus);
        verify(this.mockGateway).sendEvent(updatedReservation);
    }

    @Test
    @DisplayName("Should throw exception when reservation not found")
    void shouldThrowExceptionWhenReservationNotFound() {
        // Given
        when(this.mockGateway.findById(any(UUID.class))).thenReturn(Mono.empty());

        // When
        final Mono<ReservationEntity> result = this.useCase.execute(UUID.randomUUID(), ReservationStatus.CANCELLED);

        // Then
        StepVerifier.create(result).expectError(EntityNotFoundException.class).verify();
    }
}
