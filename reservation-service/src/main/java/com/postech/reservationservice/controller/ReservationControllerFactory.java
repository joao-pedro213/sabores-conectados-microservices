package com.postech.reservationservice.controller;

import com.postech.core.reservation.controller.ReservationController;
import com.postech.reservationservice.data.ReservationDataSourceImpl;
import com.postech.reservationservice.data.ReservationMessageProducerImpl;
import com.postech.reservationservice.data.RestaurantDataSourceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReservationControllerFactory {
    private final ReservationDataSourceImpl reservationDataSource;
    private final RestaurantDataSourceImpl restaurantDataSource;
    private final ReservationMessageProducerImpl messageProducer;

    public ReservationController build() {
        return ReservationController.build(reservationDataSource, restaurantDataSource, messageProducer);
    }
}
