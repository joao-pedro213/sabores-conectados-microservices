package com.postech.restaurantservice.controller;

import com.postech.core.restaurant.controller.RestaurantController;
import com.postech.restaurantservice.data.RestaurantDataSourceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RestaurantControllerFactory {
    private RestaurantDataSourceImpl restaurantDataSourceJpa;

    public RestaurantController build() {
        return RestaurantController.build(this.restaurantDataSourceJpa);
    }
}
