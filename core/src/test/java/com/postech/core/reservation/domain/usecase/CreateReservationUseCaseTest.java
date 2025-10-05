package com.postech.core.reservation.domain.usecase;

import com.postech.core.common.exception.EntityNotFoundException;
import com.postech.core.reservation.domain.entity.ReservationEntity;
import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import com.postech.core.reservation.domain.exception.InvalidReservationDateException;
import com.postech.core.reservation.domain.exception.ReservationOutsideBusinessHoursException;
import com.postech.core.reservation.gateway.ReservationGateway;
import com.postech.core.restaurant.domain.entity.RestaurantEntity;
import com.postech.core.restaurant.valueobject.DailySchedule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateReservationUseCaseTest {

    @Mock
    private ReservationGateway mockGateway;

    @InjectMocks
    private CreateReservationUseCase useCase;

    @Test
    @DisplayName("Should create a new reservation when time is within business hours")
    void shouldCreateReservationWhenTimeIsWithinBusinessHours() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        LocalDate reservationDate = LocalDate.now().plusDays(1);
        LocalTime reservationTime = LocalTime.of(19, 0);
        ReservationEntity newReservation = ReservationEntity
                .builder()
                .restaurantId(restaurantId)
                .date(reservationDate)
                .time(reservationTime)
                .build();
        RestaurantEntity restaurant = RestaurantEntity
                .builder()
                .id(restaurantId)
                .businessHours(
                        Map.of(
                                reservationDate.getDayOfWeek(),
                                DailySchedule
                                        .builder()
                                        .openingTime(LocalTime.of(18, 0))
                                        .closingTime(LocalTime.of(22, 0))
                                        .build()))
                .build();
        when(this.mockGateway.findRestaurantById(restaurantId)).thenReturn(Mono.just(restaurant));
        when(this.mockGateway.save(any(ReservationEntity.class))).thenReturn(Mono.just(newReservation));
        when(this.mockGateway.sendEvent(any(ReservationEntity.class))).thenReturn(Mono.empty());

        // When
        Mono<ReservationEntity> result = this.useCase.execute(newReservation);

        // Then
        StepVerifier.create(result).expectNext(newReservation).verifyComplete();
        ArgumentCaptor<ReservationEntity> captor = ArgumentCaptor.forClass(ReservationEntity.class);
        verify(this.mockGateway).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isNotNull().isEqualTo(ReservationStatus.PENDING);
        verify(this.mockGateway).sendEvent(newReservation);
    }

    @Test
    @DisplayName("Should return error when reservation date is not in the future")
    void shouldReturnErrorWhenReservationDateIsNotInTheFuture() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        LocalDate reservationDate = LocalDate.now().minusDays(1);
        LocalTime reservationTime = LocalTime.of(19, 0);
        ReservationEntity newReservation = ReservationEntity
                .builder()
                .restaurantId(restaurantId)
                .date(reservationDate)
                .time(reservationTime)
                .build();
        RestaurantEntity restaurant = RestaurantEntity.builder().id(restaurantId).build();
        when(this.mockGateway.findRestaurantById(restaurantId)).thenReturn(Mono.just(restaurant));

        // When
        Mono<ReservationEntity> result = this.useCase.execute(newReservation);

        // Then
        StepVerifier
                .create(result)
                .expectError(InvalidReservationDateException.class)
                .verify();
    }

    @Test
    @DisplayName("Should return error when restaurant is not found")
    void shouldReturnErrorWhenRestaurantNotFound() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        ReservationEntity newReservation = ReservationEntity.builder().restaurantId(restaurantId).build();
        when(this.mockGateway.findRestaurantById(restaurantId)).thenReturn(Mono.empty());

        // When
        Mono<ReservationEntity> result = this.useCase.execute(newReservation);

        // Then
        StepVerifier
                .create(result)
                .expectError(EntityNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should return error when reservation time is outside business hours")
    void shouldReturnErrorWhenReservationTimeIsOutsideBusinessHours() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        LocalDate reservationDate = LocalDate.now().plusDays(1);
        LocalTime reservationTime = LocalTime.of(23, 0);
        ReservationEntity newReservation = ReservationEntity
                .builder()
                .restaurantId(restaurantId)
                .date(reservationDate)
                .time(reservationTime)
                .build();
        RestaurantEntity restaurant = RestaurantEntity
                .builder()
                .id(restaurantId)
                .businessHours(
                        Map.of(
                                reservationDate.getDayOfWeek(),
                                DailySchedule
                                        .builder()
                                        .openingTime(LocalTime.of(18, 0))
                                        .closingTime(LocalTime.of(22, 0))
                                        .build()))
                .build();

        when(this.mockGateway.findRestaurantById(restaurantId)).thenReturn(Mono.just(restaurant));

        // When
        Mono<ReservationEntity> result = this.useCase.execute(newReservation);

        // Then
        StepVerifier
                .create(result)
                .expectError(ReservationOutsideBusinessHoursException.class)
                .verify();
    }

    @Test
    @DisplayName("Should return error when there are no business hours for the reservation day")
    void shouldReturnErrorWhenNoBusinessHoursForReservationDay() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        LocalDate reservationDate = LocalDate.now().plusDays(1);
        LocalTime reservationTime = LocalTime.of(19, 0);
        ReservationEntity newReservation = ReservationEntity
                .builder()
                .restaurantId(restaurantId)
                .date(reservationDate)
                .time(reservationTime)
                .build();
        RestaurantEntity restaurant = RestaurantEntity
                .builder()
                .id(restaurantId)
                .businessHours(Collections.emptyMap())
                .build();
        when(this.mockGateway.findRestaurantById(restaurantId)).thenReturn(Mono.just(restaurant));

        // When
        Mono<ReservationEntity> result = this.useCase.execute(newReservation);

        // Then
        StepVerifier
                .create(result)
                .expectError(ReservationOutsideBusinessHoursException.class)
                .verify();
    }
}
