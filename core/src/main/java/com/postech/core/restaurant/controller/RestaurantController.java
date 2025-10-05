package com.postech.core.restaurant.controller;

import com.postech.core.restaurant.datasource.IRestaurantDataSource;
import com.postech.core.restaurant.domain.entity.RestaurantEntity;
import com.postech.core.restaurant.domain.usecase.CreateRestaurantUseCase;
import com.postech.core.restaurant.domain.usecase.DeleteRestaurantByIdUseCase;
import com.postech.core.restaurant.domain.usecase.RetrieveRestaurantByIdUseCase;
import com.postech.core.restaurant.domain.usecase.UpdateRestaurantUseCase;
import com.postech.core.restaurant.dto.NewRestaurantDto;
import com.postech.core.restaurant.dto.RestaurantDto;
import com.postech.core.restaurant.dto.UpdateRestaurantDto;
import com.postech.core.restaurant.gateway.RestaurantGateway;
import com.postech.core.restaurant.presenter.RestaurantPresenter;

import java.util.UUID;

public class RestaurantController {
    private final RestaurantGateway restaurantGateway;

    public RestaurantController(IRestaurantDataSource restaurantDataSource) {
        this.restaurantGateway = RestaurantGateway.build(restaurantDataSource);
    }

    public static RestaurantController build(IRestaurantDataSource restaurantDataSource) {
        return new RestaurantController(restaurantDataSource);
    }

    public RestaurantDto createRestaurant(NewRestaurantDto newRestaurantDto) {
        CreateRestaurantUseCase useCase = CreateRestaurantUseCase.build(this.restaurantGateway);
        RestaurantEntity createdRestaurant = useCase.execute(this.toDomain(newRestaurantDto));
        RestaurantPresenter presenter = RestaurantPresenter.build();
        return presenter.toDto(createdRestaurant);
    }

    public RestaurantDto retrieveRestaurantById(UUID id) {
        RetrieveRestaurantByIdUseCase useCase = RetrieveRestaurantByIdUseCase.build(this.restaurantGateway);
        RestaurantEntity foundRestaurant = useCase.execute(id);
        RestaurantPresenter presenter = RestaurantPresenter.build();
        return presenter.toDto(foundRestaurant);
    }

    public RestaurantDto updateRestaurant(UUID id, UpdateRestaurantDto updateRestaurantDto) {
        RestaurantEntity updatedRestaurant = UpdateRestaurantUseCase
                .build(this.restaurantGateway)
                .execute(
                        id,
                        updateRestaurantDto.getManagerIds(),
                        updateRestaurantDto.getAddress(),
                        updateRestaurantDto.getBusinessHours());
        return RestaurantPresenter.build().toDto(updatedRestaurant);
    }

    public void deleteRestaurantById(UUID id) {
        DeleteRestaurantByIdUseCase useCase = DeleteRestaurantByIdUseCase.build(this.restaurantGateway);
        useCase.execute(id);
    }

    private RestaurantEntity toDomain(NewRestaurantDto newRestaurantDto) {
        return RestaurantEntity
                .builder()
                .ownerId(newRestaurantDto.getOwnerId())
                .managerIds(newRestaurantDto.getManagerIds())
                .name(newRestaurantDto.getName())
                .address(newRestaurantDto.getAddress())
                .cuisineType(newRestaurantDto.getCuisineType())
                .businessHours(newRestaurantDto.getBusinessHours())
                .build();
    }
}
