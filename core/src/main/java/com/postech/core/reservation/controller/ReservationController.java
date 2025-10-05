package com.postech.core.reservation.controller;

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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class ReservationController {
    private final ReservationGateway gateway;

    public ReservationController(
            IReservationDataSource reservationDataSource,
            IRestaurantDataSource restaurantDataSource,
            IReservationMessageProducer messageProducer) {
        this.gateway = ReservationGateway.build(reservationDataSource, restaurantDataSource, messageProducer);
    }

    public static ReservationController build(
            IReservationDataSource reservationDataSource,
            IRestaurantDataSource restaurantDataSource,
            IReservationMessageProducer messageProducer) {
        return new ReservationController(reservationDataSource, restaurantDataSource, messageProducer);
    }

    public Mono<ReservationDto> createReservation(NewReservationDto newReservationDto) {
        return CreateReservationUseCase
                .build(this.gateway)
                .execute(toDomain(newReservationDto))
                .map(ReservationPresenter.build()::toDto);
    }

    public Flux<ReservationDto> retrieveReservationByRestaurantId(UUID restaurantId, ReservationStatus status) {
        return RetrieveReservationsByRestaurantIdUseCase
                .build(this.gateway)
                .execute(restaurantId, status)
                .map(ReservationPresenter.build()::toDto);
    }

    public Mono<ReservationDto> changeReservationStatus(UUID id, ReservationStatus newStatus) {
        return ChangeReservationStatusUseCase
                .build(this.gateway)
                .execute(id, newStatus)
                .map(ReservationPresenter.build()::toDto);
    }

    private static ReservationEntity toDomain(NewReservationDto newReservationDto) {
        return ReservationEntity
                .builder()
                .restaurantId(newReservationDto.getRestaurantId())
                .customerId(newReservationDto.getCustomerId())
                .people(newReservationDto.getPeople())
                .date(newReservationDto.getDate())
                .time(newReservationDto.getTime())
                .build();
    }
}
