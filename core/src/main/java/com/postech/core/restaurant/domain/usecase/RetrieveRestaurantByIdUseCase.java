package com.postech.core.restaurant.domain.usecase;

import com.postech.core.restaurant.domain.entity.RestaurantEntity;
import com.postech.core.common.exception.EntityNotFoundException;
import com.postech.core.restaurant.gateway.RestaurantGateway;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class RetrieveRestaurantByIdUseCase {
    private final RestaurantGateway restaurantGateway;

    public static RetrieveRestaurantByIdUseCase build(RestaurantGateway restaurantGateway) {
        return new RetrieveRestaurantByIdUseCase(restaurantGateway);
    }

    public RestaurantEntity execute(UUID id) {
        return this.restaurantGateway.findById(id).orElseThrow(() -> new EntityNotFoundException("Restaurant"));
    }
}
