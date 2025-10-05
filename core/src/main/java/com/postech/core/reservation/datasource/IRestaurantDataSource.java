package com.postech.core.reservation.datasource;

import com.postech.core.restaurant.dto.RestaurantDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IRestaurantDataSource {
    Mono<RestaurantDto> findById(UUID id);
}
