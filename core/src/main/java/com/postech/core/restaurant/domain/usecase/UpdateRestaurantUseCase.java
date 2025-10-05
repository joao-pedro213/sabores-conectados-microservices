package com.postech.core.restaurant.domain.usecase;

import com.postech.core.common.exception.EntityNotFoundException;
import com.postech.core.restaurant.domain.entity.RestaurantEntity;
import com.postech.core.restaurant.gateway.RestaurantGateway;
import com.postech.core.restaurant.valueobject.DailySchedule;
import lombok.AllArgsConstructor;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class UpdateRestaurantUseCase {
    private final RestaurantGateway restaurantGateway;

    public static UpdateRestaurantUseCase build(RestaurantGateway restaurantGateway) {
        return new UpdateRestaurantUseCase(restaurantGateway);
    }

    public RestaurantEntity execute(
            UUID id,
            List<UUID> managerIds,
            String address,
            Map<DayOfWeek, DailySchedule> businessHours) {
        RestaurantEntity foundRestaurant = this.restaurantGateway
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant"));
        RestaurantEntity restaurantWithUpdates = foundRestaurant
                .toBuilder()
                .managerIds(managerIds)
                .address(address)
                .businessHours(businessHours)
                .build();
        return this.restaurantGateway.save(restaurantWithUpdates);
    }
}
