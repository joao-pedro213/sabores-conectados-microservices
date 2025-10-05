package com.postech.core.restaurant.presenter;

import com.postech.core.restaurant.domain.entity.RestaurantEntity;
import com.postech.core.restaurant.dto.RestaurantDto;

public class RestaurantPresenter {
    public static RestaurantPresenter build() {
        return new RestaurantPresenter();
    }

    public RestaurantDto toDto(RestaurantEntity restaurant) {
        return RestaurantDto
                .builder()
                .id(restaurant.getId())
                .ownerId(restaurant.getOwnerId())
                .managerIds(restaurant.getManagerIds())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .cuisineType(restaurant.getCuisineType())
                .businessHours(restaurant.getBusinessHours())
                .lastUpdated(restaurant.getLastUpdated())
                .build();
    }
}
