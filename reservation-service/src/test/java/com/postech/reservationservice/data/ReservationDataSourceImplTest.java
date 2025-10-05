package com.postech.reservationservice.data;

import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import com.postech.core.reservation.dto.ReservationDto;
import com.postech.reservationservice.data.document.ReservationDocument;
import com.postech.reservationservice.data.repository.IReservationRepository;
import com.postech.reservationservice.mapper.IReservationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationDataSourceImplTest {

    @Mock
    private IReservationRepository mockRepository;

    @Mock
    private IReservationMapper mockMapper;

    @InjectMocks
    private ReservationDataSourceImpl dataSource;

    @Test
    void shouldSaveReservation() {
        // Given
        final ReservationDto reservationToSaveDto = ReservationDto.builder().build();
        final ReservationDocument reservationToSave = ReservationDocument.builder().build();
        final ReservationDocument savedReservation = ReservationDocument.builder().id(UUID.randomUUID()).build();
        final ReservationDto expectedSavedReservationDto = ReservationDto.builder().id(savedReservation.getId()).build();
        when(this.mockMapper.toReservationDocument(any(ReservationDto.class))).thenReturn(reservationToSave);
        when(this.mockRepository.save(reservationToSave)).thenReturn(Mono.just(savedReservation));
        when(this.mockMapper.toReservationDto(savedReservation)).thenReturn(expectedSavedReservationDto);

        // When
        Mono<ReservationDto> result = this.dataSource.save(reservationToSaveDto);

        // Then
        StepVerifier.create(result).expectNext(expectedSavedReservationDto).verifyComplete();
    }

    @Test
    void shouldFindReservationById() {
        // Given
        final UUID id = UUID.randomUUID();
        final ReservationDocument foundReservation = ReservationDocument.builder().id(id).build();
        final ReservationDto mappedReservationDto = ReservationDto.builder().id(id).build();
        when(this.mockRepository.findById(id)).thenReturn(Mono.just(foundReservation));
        when(this.mockMapper.toReservationDto(foundReservation)).thenReturn(mappedReservationDto);

        // When
        Mono<ReservationDto> result = this.dataSource.findById(id);

        // Then
        StepVerifier.create(result).expectNext(mappedReservationDto).verifyComplete();
    }

    @Test
    void shouldFindAllReservationsByRestaurantId() {
        // Given
        final UUID restaurantId = UUID.randomUUID();
        final ReservationDocument reservation1 = ReservationDocument.builder().id(UUID.randomUUID()).build();
        final ReservationDocument reservation2 = ReservationDocument.builder().id(UUID.randomUUID()).build();
        final ReservationDto dto1 = ReservationDto.builder().id(reservation1.getId()).build();
        final ReservationDto dto2 = ReservationDto.builder().id(reservation2.getId()).build();
        when(this.mockRepository.findAllByRestaurantId(restaurantId)).thenReturn(Flux.just(reservation1, reservation2));
        when(this.mockMapper.toReservationDto(reservation1)).thenReturn(dto1);
        when(this.mockMapper.toReservationDto(reservation2)).thenReturn(dto2);

        // When
        Flux<ReservationDto> result = this.dataSource.findAllByRestaurantId(restaurantId);

        // Then
        StepVerifier.create(result).expectNext(dto1, dto2).verifyComplete();
    }

    @Test
    void shouldFindAllReservationsByRestaurantIdAndStatus() {
        // Given
        final UUID restaurantId = UUID.randomUUID();
        final ReservationStatus status = ReservationStatus.PENDING;
        final ReservationDocument reservation1 = ReservationDocument.builder().id(UUID.randomUUID()).build();
        final ReservationDocument reservation2 = ReservationDocument.builder().id(UUID.randomUUID()).build();
        final ReservationDto dto1 = ReservationDto.builder().id(reservation1.getId()).build();
        final ReservationDto dto2 = ReservationDto.builder().id(reservation2.getId()).build();
        when(this.mockRepository.findAllByRestaurantIdAndStatus(restaurantId, status)).thenReturn(Flux.just(reservation1, reservation2));
        when(this.mockMapper.toReservationDto(reservation1)).thenReturn(dto1);
        when(this.mockMapper.toReservationDto(reservation2)).thenReturn(dto2);

        // When
        Flux<ReservationDto> result = this.dataSource.findAllByRestaurantIdAndStatus(restaurantId, status);

        // Then
        StepVerifier.create(result).expectNext(dto1, dto2).verifyComplete();
    }
}