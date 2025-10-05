package com.postech.core.reservation.gateway;

import com.postech.core.helpers.ReservationObjectMother;
import com.postech.core.reservation.datasource.IReservationDataSource;
import com.postech.core.reservation.datasource.IReservationMessageProducer;
import com.postech.core.reservation.datasource.IRestaurantDataSource;
import com.postech.core.reservation.domain.entity.ReservationEntity;
import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import com.postech.core.reservation.dto.ReservationDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationGatewayTest {

    @Mock
    private IReservationDataSource dataSource;

    @Mock
    private IRestaurantDataSource restaurantDataSource;

    @Mock
    private IReservationMessageProducer messageProducer;

    @InjectMocks
    private ReservationGateway gateway;

    private static Map<String, Object> getSampleReservationData() {
        return Map.of(
                "id", UUID.randomUUID(),
                "restaurantId", UUID.randomUUID(),
                "customerId", UUID.randomUUID(),
                "status", "PENDING",
                "people", 4,
                "date", LocalDate.now().plusDays(1),
                "time", LocalTime.of(20, 0),
                "createdAt", LocalDateTime.now()
        );
    }

    @Test
    void shouldSaveReservation() {
        // Given
        final Map<String, Object> reservationSampleData = getSampleReservationData();
        final ReservationEntity reservationToSave = ReservationObjectMother.buildReservationEntity(reservationSampleData);
        final ReservationDto savedReservationDto = ReservationObjectMother.buildReservationDto(reservationSampleData);
        when(this.dataSource.save(any(ReservationDto.class))).thenReturn(Mono.just(savedReservationDto));

        // When
        final Mono<ReservationEntity> result = this.gateway.save(reservationToSave);

        // Then
        StepVerifier
                .create(result)
                .expectNextMatches(savedReservation -> {
                    final ArgumentCaptor<ReservationDto> argument = ArgumentCaptor.forClass(ReservationDto.class);
                    verify(this.dataSource).save(argument.capture());
                    final ReservationDto capturedReservationDto = argument.getValue();
                    final ReservationDto expectedReservationDto = ReservationObjectMother.buildReservationDto(reservationSampleData);
                    assertThat(capturedReservationDto).usingRecursiveComparison().isEqualTo(expectedReservationDto);
                    assertThat(savedReservation).isNotNull();
                    final ReservationEntity expectedUpdatedReservation = reservationToSave.toBuilder().build();
                    assertThat(savedReservation).usingRecursiveComparison().isEqualTo(expectedUpdatedReservation);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldFindReservationById() {
        // Given
        final Map<String, Object> reservationSampleData = getSampleReservationData();
        final ReservationDto foundReservationDto = ReservationObjectMother.buildReservationDto(reservationSampleData);
        final UUID id = (UUID) reservationSampleData.get("id");
        when(this.dataSource.findById(id)).thenReturn(Mono.just(foundReservationDto));

        // When
        final Mono<ReservationEntity> result = this.gateway.findById(id);

        // Then
        StepVerifier
                .create(result)
                .expectNextMatches(foundReservation -> {
                    final ReservationEntity expectedFoundReservation = ReservationObjectMother.buildReservationEntity(reservationSampleData);
                    assertThat(foundReservation).usingRecursiveComparison().isEqualTo(expectedFoundReservation);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldFindAllByRestaurantId() {
        // Given
        final Map<String, Object> reservationSampleData = getSampleReservationData();
        final List<ReservationDto> foundReservationDtos = List.of(ReservationObjectMother.buildReservationDto(reservationSampleData));
        final UUID restaurantId = (UUID) reservationSampleData.get("restaurantId");
        when(this.dataSource.findAllByRestaurantId(restaurantId)).thenReturn(Flux.fromIterable(foundReservationDtos));

        // When
        final Flux<ReservationEntity> result = this.gateway.findAllByRestaurantId(restaurantId);

        // Then
        StepVerifier
                .create(result)
                .expectNextMatches(foundReservation -> {
                    final ReservationEntity expectedFoundReservation = ReservationObjectMother.buildReservationEntity(reservationSampleData);
                    assertThat(foundReservation).usingRecursiveComparison().isEqualTo(expectedFoundReservation);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldFindAllByRestaurantIdAndStatus() {
        // Given
        final Map<String, Object> reservationSampleData = getSampleReservationData();
        final List<ReservationDto> foundReservationDtos = List.of(ReservationObjectMother.buildReservationDto(reservationSampleData));
        final UUID restaurantId = (UUID) reservationSampleData.get("restaurantId");
        final ReservationStatus status = ReservationStatus.valueOf((String) reservationSampleData.get("status"));
        when(this.dataSource.findAllByRestaurantIdAndStatus(restaurantId, status)).thenReturn(Flux.fromIterable(foundReservationDtos));

        // When
        final Flux<ReservationEntity> result = this.gateway.findAllByRestaurantIdAndStatus(restaurantId, status);

        // Then
        StepVerifier
                .create(result)
                .expectNextMatches(foundReservation -> {
                    final ReservationEntity expectedFoundReservation = ReservationObjectMother.buildReservationEntity(reservationSampleData);
                    assertThat(foundReservation).usingRecursiveComparison().isEqualTo(expectedFoundReservation);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldSendEvent() {
        // Given
        final Map<String, Object> reservationSampleData = getSampleReservationData();
        final ReservationEntity reservationEntity = ReservationObjectMother.buildReservationEntity(reservationSampleData);
        when(this.messageProducer.sendMessage(any(ReservationDto.class))).thenReturn(Mono.empty());

        // When
        final Mono<Void> result = this.gateway.sendEvent(reservationEntity);

        // Then
        StepVerifier.create(result).verifyComplete();
        final ArgumentCaptor<ReservationDto> argument = ArgumentCaptor.forClass(ReservationDto.class);
        verify(this.messageProducer).sendMessage(argument.capture());
        final ReservationDto capturedReservationDto = argument.getValue();
        final ReservationDto expectedReservationDto = ReservationObjectMother.buildReservationDto(reservationSampleData);
        assertThat(capturedReservationDto).usingRecursiveComparison().isEqualTo(expectedReservationDto);
    }
}
