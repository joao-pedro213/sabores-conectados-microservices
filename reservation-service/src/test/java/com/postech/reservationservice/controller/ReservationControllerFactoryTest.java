package com.postech.reservationservice.controller;

import com.postech.core.reservation.controller.ReservationController;
import com.postech.reservationservice.data.ReservationDataSourceImpl;
import com.postech.reservationservice.data.ReservationMessageProducerImpl;
import com.postech.reservationservice.data.RestaurantDataSourceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ReservationControllerFactoryTest {

    @Mock
    private ReservationDataSourceImpl reservationDataSource;

    @Mock
    private RestaurantDataSourceImpl restaurantDataSource;

    @Mock
    private ReservationMessageProducerImpl messageProducer;

    @InjectMocks
    private ReservationControllerFactory reservationControllerFactory;

    @Test
    void shouldBuildReservationControllerSuccessfully() {
        // Given
        ReservationController expectedController = mock(ReservationController.class);
        try (MockedStatic<ReservationController> mockedStatic = Mockito.mockStatic(ReservationController.class)) {
            mockedStatic.when(() ->
                            ReservationController.build(this.reservationDataSource, this.restaurantDataSource, this.messageProducer))
                    .thenReturn(expectedController);

            // When
            ReservationController actualController = this.reservationControllerFactory.build();

            // Then
            assertThat(actualController).isNotNull().isEqualTo(expectedController);
        }
    }
}
