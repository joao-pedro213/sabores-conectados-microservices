package com.postech.core.restaurant.domain.usecase;

import com.postech.core.common.exception.EntityAlreadyExistsException;
import com.postech.core.restaurant.gateway.RestaurantGateway;
import com.postech.core.restaurant.domain.entity.RestaurantEntity;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class CreateRestaurantUseCase {
    private final RestaurantGateway restaurantGateway;

    public static CreateRestaurantUseCase build(RestaurantGateway restaurantGateway) {
        return new CreateRestaurantUseCase(restaurantGateway);
    }

    public RestaurantEntity execute(RestaurantEntity restaurantEntity) {
        Optional<RestaurantEntity> foundRestaurant = this.restaurantGateway.findByName(restaurantEntity.getName());
        if (foundRestaurant.isPresent()) {
            throw new EntityAlreadyExistsException("Restaurant");
        }
        return this.restaurantGateway.save(restaurantEntity);
    }
}
