package com.postech.core.restaurant.datasource;

import com.postech.core.restaurant.dto.RestaurantDto;

import java.util.Optional;
import java.util.UUID;

public interface IRestaurantDataSource {
    RestaurantDto save(RestaurantDto restaurantDto);

    Optional<RestaurantDto> findById(UUID id);

    Optional<RestaurantDto> findByName(String name);

    void deleteById(UUID id);
}
