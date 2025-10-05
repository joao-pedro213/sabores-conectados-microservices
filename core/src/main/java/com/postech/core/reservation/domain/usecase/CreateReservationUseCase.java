package com.postech.core.reservation.domain.usecase;

import com.postech.core.common.exception.EntityNotFoundException;
import com.postech.core.reservation.domain.entity.ReservationEntity;
import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import com.postech.core.reservation.domain.exception.InvalidReservationDateException;
import com.postech.core.reservation.domain.exception.ReservationOutsideBusinessHoursException;
import com.postech.core.reservation.gateway.ReservationGateway;
import com.postech.core.restaurant.domain.entity.RestaurantEntity;
import com.postech.core.restaurant.valueobject.DailySchedule;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@AllArgsConstructor
public class CreateReservationUseCase {
    private final ReservationGateway gateway;

    public static CreateReservationUseCase build(ReservationGateway gateway) {
        return new CreateReservationUseCase(gateway);
    }

    public Mono<ReservationEntity> execute(ReservationEntity reservationEntity) {
        reservationEntity.setStatus(ReservationStatus.PENDING);
        return this.gateway
                .findRestaurantById(reservationEntity.getRestaurantId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Restaurant")))
                .flatMap(restaurantEntity -> {
                    if (!isReservationDateValid(reservationEntity)) {
                        return Mono.error(new InvalidReservationDateException());
                    }
                    if (isReservationTimeValid(reservationEntity, restaurantEntity)) {
                        return this.gateway
                                .save(reservationEntity)
                                .flatMap(savedReservation ->
                                        this.gateway.sendEvent(savedReservation).thenReturn(savedReservation));
                    } else {
                        return Mono.error(new ReservationOutsideBusinessHoursException());
                    }
                });
    }

    private static boolean isReservationDateValid(ReservationEntity reservationEntity) {
        LocalDateTime reservationDateTime = LocalDateTime.of(reservationEntity.getDate(), reservationEntity.getTime());
        return reservationDateTime.isAfter(LocalDateTime.now());
    }

    private static boolean isReservationTimeValid(ReservationEntity reservationEntity, RestaurantEntity restaurantEntity) {
        DayOfWeek reservationDayOfWeek = reservationEntity.getDate().getDayOfWeek();
        DailySchedule restaurantSchedule = restaurantEntity.getBusinessHours().get(reservationDayOfWeek);
        if (restaurantSchedule == null) {
            return false;
        }
        return !reservationEntity.getTime().isBefore(restaurantSchedule.getOpeningTime())
                && !reservationEntity.getTime().isAfter(restaurantSchedule.getClosingTime());
    }
}
