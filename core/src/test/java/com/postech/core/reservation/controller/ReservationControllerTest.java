package com.postech.core.reservation.controller;

import com.postech.core.helpers.ReservationObjectMother;
import com.postech.core.reservation.datasource.IReservationDataSource;
import com.postech.core.reservation.datasource.IReservationMessageProducer;
import com.postech.core.reservation.datasource.IRestaurantDataSource;
import com.postech.core.reservation.domain.entity.ReservationEntity;
import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import com.postech.core.reservation.domain.usecase.ChangeReservationStatusUseCase;
import com.postech.core.reservation.domain.usecase.CreateReservationUseCase;
import com.postech.core.reservation.domain.usecase.RetrieveReservationsByRestaurantIdUseCase;
import com.postech.core.reservation.dto.NewReservationDto;
import com.postech.core.reservation.dto.ReservationDto;
import com.postech.core.reservation.gateway.ReservationGateway;
import com.postech.core.reservation.presenter.ReservationPresenter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private IReservationDataSource mockReservationDataSource;

    @Mock
    private IRestaurantDataSource mockRestaurantDataSource;

    @Mock
    private IReservationMessageProducer mockReservationMessageProducer;

    @InjectMocks
    private ReservationController controller;

    @Mock
    private ReservationPresenter mockReservationPresenter;

    private MockedStatic<ReservationPresenter> mockedStaticReservationPresenter;

    @BeforeEach
    void setUp() {
        this.mockedStaticReservationPresenter = mockStatic(ReservationPresenter.class);
        this.mockedStaticReservationPresenter.when(ReservationPresenter::build).thenReturn(this.mockReservationPresenter);
    }

    private static Map<String, Object> getSampleReservationData() {
        return Map.of(
                "id", UUID.randomUUID(),
                "restaurantId", UUID.randomUUID(),
                "customerId", UUID.randomUUID(),
                "status", "CONFIRMED",
                "people", 4,
                "date", LocalDate.now().plusDays(1),
                "time", LocalTime.of(20, 0),
                "createdAt", LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateReservation() {
        // Given
        final Map<String, Object> reservationSampleData = getSampleReservationData();
        final NewReservationDto newReservationDto = ReservationObjectMother.buildNewReservationDto(reservationSampleData);
        final ReservationEntity createdReservationEntity = ReservationObjectMother.buildReservationEntity(reservationSampleData);
        final ReservationDto createdReservationDto = ReservationDto.builder().build();
        final CreateReservationUseCase mockCreateReservationUseCase = mock(CreateReservationUseCase.class);
        when(mockCreateReservationUseCase.execute(any(ReservationEntity.class))).thenReturn(Mono.just(createdReservationEntity));
        when(this.mockReservationPresenter.toDto(createdReservationEntity)).thenReturn(createdReservationDto);
        try (MockedStatic<CreateReservationUseCase> mockedStaticCreateReservationUseCase = mockStatic(CreateReservationUseCase.class)) {
            mockedStaticCreateReservationUseCase.when(() -> CreateReservationUseCase.build(any(ReservationGateway.class))).thenReturn(mockCreateReservationUseCase);

            // When
            final Mono<ReservationDto> result = this.controller.createReservation(newReservationDto);

            // Then
            StepVerifier
                    .create(result)
                    .expectNextMatches(reservationDto -> {
                        assertThat(reservationDto).isNotNull().isEqualTo(createdReservationDto);
                        return true;
                    })
                    .verifyComplete();
        }
    }

    @Test
    void shouldRetrieveReservationsByRestaurantId() {
        // Given
        final Map<String, Object> reservationSampleData = getSampleReservationData();
        final UUID restaurantId = (UUID) reservationSampleData.get("restaurantId");
        final ReservationStatus status = ReservationStatus.valueOf((String) reservationSampleData.get("status"));
        final List<ReservationEntity> foundReservations = List.of(ReservationObjectMother.buildReservationEntity(reservationSampleData));
        final RetrieveReservationsByRestaurantIdUseCase mockRetrieveReservationsUseCase = mock(RetrieveReservationsByRestaurantIdUseCase.class);
        when(mockRetrieveReservationsUseCase.execute(restaurantId, status)).thenReturn(Flux.fromIterable(foundReservations));
        final ReservationDto foundReservationDto = ReservationDto.builder().build();
        when(this.mockReservationPresenter.toDto(any(ReservationEntity.class))).thenReturn(foundReservationDto);
        try (MockedStatic<RetrieveReservationsByRestaurantIdUseCase> mockedStaticRetrieveReservationsUseCase = mockStatic(RetrieveReservationsByRestaurantIdUseCase.class)) {
            mockedStaticRetrieveReservationsUseCase.when(() -> RetrieveReservationsByRestaurantIdUseCase.build(any(ReservationGateway.class))).thenReturn(mockRetrieveReservationsUseCase);

            // When
            final Flux<ReservationDto> result = this.controller.retrieveReservationByRestaurantId(restaurantId, status);

            // Then
            StepVerifier
                    .create(result)
                    .expectNextMatches(reservationDto -> {
                        assertThat(reservationDto).isNotNull().isEqualTo(foundReservationDto);
                        return true;
                    })
                    .verifyComplete();
        }
    }

    @Test
    void shouldChangeReservationStatus() {
        // Given
        final UUID reservationId = UUID.randomUUID();
        final ReservationStatus newStatus = ReservationStatus.CONFIRMED;
        final Map<String, Object> reservationSampleData = getSampleReservationData();
        final ReservationEntity updatedReservationEntity = ReservationObjectMother.buildReservationEntity(reservationSampleData);
        final ChangeReservationStatusUseCase mockChangeReservationStatusUseCase = mock(ChangeReservationStatusUseCase.class);
        when(mockChangeReservationStatusUseCase.execute(reservationId, newStatus)).thenReturn(Mono.just(updatedReservationEntity));
        final ReservationDto updatedReservationDto = ReservationDto.builder().build();
        when(this.mockReservationPresenter.toDto(updatedReservationEntity)).thenReturn(updatedReservationDto);
        try (MockedStatic<ChangeReservationStatusUseCase> mockedStaticChangeReservationStatusUseCase = mockStatic(ChangeReservationStatusUseCase.class)) {
            mockedStaticChangeReservationStatusUseCase.when(() -> ChangeReservationStatusUseCase.build(any(ReservationGateway.class))).thenReturn(mockChangeReservationStatusUseCase);

            // When
            final Mono<ReservationDto> result = this.controller.changeReservationStatus(reservationId, newStatus);

            // Then
            StepVerifier
                    .create(result)
                    .expectNextMatches(reservationDto -> {
                        assertThat(reservationDto).isNotNull().isEqualTo(updatedReservationDto);
                        return true;
                    })
                    .verifyComplete();
        }
    }

    @AfterEach
    void tearDown() {
        if (this.mockedStaticReservationPresenter != null) {
            this.mockedStaticReservationPresenter.close();
        }
    }
}
