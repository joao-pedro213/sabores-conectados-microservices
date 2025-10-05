package com.postech.core.reservation.presenter;

import com.postech.core.helpers.ReservationObjectMother;
import com.postech.core.reservation.domain.entity.ReservationEntity;
import com.postech.core.reservation.dto.ReservationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationPresenterTest {

    private ReservationPresenter presenter;

    @BeforeEach
    void setUp() {
        presenter = ReservationPresenter.build();
    }

    private static Map<String, Object> getSampleReservationData() {
        return Map.of(
                "id", UUID.randomUUID(),
                "restaurantId", UUID.randomUUID(),
                "customerId", UUID.randomUUID(),
                "status", "PENDING",
                "people", 1,
                "date", LocalDate.now().plusDays(1),
                "time", LocalTime.of(17, 0),
                "createdAt", LocalDateTime.now()
        );
    }

    @Test
    void shouldMapDomainToDto() {
        // Given
        Map<String, Object> reservationSampleData = getSampleReservationData();
        ReservationEntity reservationEntity = ReservationObjectMother.buildReservationEntity(reservationSampleData);

        // When
        ReservationDto reservationDto = this.presenter.toDto(reservationEntity);

        // Then
        ReservationDto expectedDto = ReservationObjectMother.buildReservationDto(reservationSampleData);
        assertThat(reservationDto).usingRecursiveComparison().isEqualTo(expectedDto);
    }
}
