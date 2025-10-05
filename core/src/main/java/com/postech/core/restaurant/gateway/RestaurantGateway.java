package com.postech.core.restaurant.gateway;

import com.postech.core.restaurant.datasource.IRestaurantDataSource;
import com.postech.core.restaurant.domain.entity.RestaurantEntity;
import com.postech.core.restaurant.dto.RestaurantDto;
import lombok.AllArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class RestaurantGateway {
    private final IRestaurantDataSource dataSource;

    public static RestaurantGateway build(IRestaurantDataSource dataSource) {
        return new RestaurantGateway(dataSource);
    }

    public RestaurantEntity save(RestaurantEntity restaurantEntity) {
        RestaurantDto restaurantToSave = this.toDto(restaurantEntity);
        RestaurantDto savedRestaurant = this.dataSource.save(restaurantToSave);
        return this.toEntity(savedRestaurant);
    }

    public Optional<RestaurantEntity> findById(UUID id) {
        Optional<RestaurantDto> foundRestaurant = this.dataSource.findById(id);
        return foundRestaurant.map(this::toEntity);
    }

    public Optional<RestaurantEntity> findByName(String name) {
        Optional<RestaurantDto> foundRestaurant = this.dataSource.findByName(name);
        return foundRestaurant.map(this::toEntity);
    }

    public void deleteById(UUID id) {
        this.dataSource.deleteById(id);
    }

    private RestaurantDto toDto(RestaurantEntity restaurantEntity) {
        return RestaurantDto
                .builder()
                .id(restaurantEntity.getId())
                .ownerId(restaurantEntity.getOwnerId())
                .managerIds(restaurantEntity.getManagerIds())
                .name(restaurantEntity.getName())
                .address(restaurantEntity.getAddress())
                .cuisineType(restaurantEntity.getCuisineType())
                .businessHours(restaurantEntity.getBusinessHours())
                .lastUpdated(restaurantEntity.getLastUpdated())
                .build();
    }

    private RestaurantEntity toEntity(RestaurantDto restaurantDto) {
        return RestaurantEntity
                .builder()
                .id(restaurantDto.getId())
                .ownerId(restaurantDto.getOwnerId())
                .managerIds(restaurantDto.getManagerIds())
                .name(restaurantDto.getName())
                .address(restaurantDto.getAddress())
                .cuisineType(restaurantDto.getCuisineType())
                .businessHours(restaurantDto.getBusinessHours())
                .lastUpdated(restaurantDto.getLastUpdated())
                .build();
    }
}
