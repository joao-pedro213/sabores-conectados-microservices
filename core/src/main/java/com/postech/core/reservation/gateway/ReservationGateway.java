package com.postech.core.reservation.gateway;

import com.postech.core.reservation.datasource.IReservationDataSource;
import com.postech.core.reservation.datasource.IReservationMessageProducer;
import com.postech.core.reservation.datasource.IRestaurantDataSource;
import com.postech.core.reservation.domain.entity.ReservationEntity;
import com.postech.core.reservation.domain.entity.enumerator.ReservationStatus;
import com.postech.core.reservation.dto.ReservationDto;
import com.postech.core.restaurant.domain.entity.RestaurantEntity;
import com.postech.core.restaurant.dto.RestaurantDto;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@AllArgsConstructor
public class ReservationGateway {
    private final IReservationDataSource reservationDataSource;
    private final IRestaurantDataSource restaurantDataSource;
    private final IReservationMessageProducer messageProducer;

    public static ReservationGateway build(
            IReservationDataSource reservationDataSource,
            IRestaurantDataSource restaurantDataSource,
            IReservationMessageProducer messageProducer) {
        return new ReservationGateway(reservationDataSource, restaurantDataSource, messageProducer);
    }

    public Mono<RestaurantEntity> findRestaurantById(UUID id) {
        return this.restaurantDataSource.findById(id).map(ReservationGateway::toRestaurantDomain);
    }

    public Mono<ReservationEntity> findById(UUID id) {
        return this.reservationDataSource.findById(id).map(ReservationGateway::toReservationDomain);
    }

    public Flux<ReservationEntity> findAllByRestaurantId(UUID restaurantId) {
        return this.reservationDataSource
                .findAllByRestaurantId(restaurantId)
                .map(ReservationGateway::toReservationDomain);
    }

    public Flux<ReservationEntity> findAllByRestaurantIdAndStatus(UUID restaurantId, ReservationStatus status) {
        return this.reservationDataSource
                .findAllByRestaurantIdAndStatus(restaurantId, status)
                .map(ReservationGateway::toReservationDomain);
    }

    public Mono<ReservationEntity> save(ReservationEntity reservation) {
        return this.reservationDataSource.save(toReservationDto(reservation)).map(ReservationGateway::toReservationDomain);
    }

    public Mono<Void> sendEvent(ReservationEntity reservationEntity) {
        return this.messageProducer.sendMessage(toReservationDto(reservationEntity));
    }

    private static RestaurantEntity toRestaurantDomain(RestaurantDto restaurantDto) {
        return RestaurantEntity
                .builder()
                .id(restaurantDto.getId())
                .ownerId(restaurantDto.getOwnerId())
                .name(restaurantDto.getName())
                .address(restaurantDto.getAddress())
                .cuisineType(restaurantDto.getCuisineType())
                .businessHours(restaurantDto.getBusinessHours())
                .lastUpdated(restaurantDto.getLastUpdated())
                .build();
    }

    private static ReservationDto toReservationDto(ReservationEntity reservation) {
        return ReservationDto.builder()
                .id(reservation.getId())
                .restaurantId(reservation.getRestaurantId())
                .customerId(reservation.getCustomerId())
                .status(reservation.getStatus())
                .people(reservation.getPeople())
                .date(reservation.getDate())
                .time(reservation.getTime())
                .createdAt(reservation.getCreatedAt())
                .build();
    }

    private static ReservationEntity toReservationDomain(ReservationDto reservation) {
        return ReservationEntity.builder()
                .id(reservation.getId())
                .restaurantId(reservation.getRestaurantId())
                .customerId(reservation.getCustomerId())
                .status(reservation.getStatus())
                .people(reservation.getPeople())
                .date(reservation.getDate())
                .time(reservation.getTime())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}
