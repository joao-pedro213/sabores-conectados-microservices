package com.postech.restaurantservice.data;

import com.postech.core.restaurant.datasource.IRestaurantDataSource;
import com.postech.core.restaurant.dto.RestaurantDto;
import com.postech.restaurantservice.data.mapper.IRestaurantMapper;
import com.postech.restaurantservice.data.document.RestaurantDocument;
import com.postech.restaurantservice.data.repository.IRestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class RestaurantDataSourceImpl implements IRestaurantDataSource {
    private final IRestaurantRepository repository;
    private final IRestaurantMapper mapper;

    @Override
    public RestaurantDto save(RestaurantDto restaurantDto) {
        RestaurantDocument restaurantToSave = this.mapper.toRestaurantDocument(restaurantDto);
        RestaurantDocument savedRestaurant = this.repository.save(restaurantToSave);
        return this.mapper.toRestaurantDto(savedRestaurant);
    }

    @Override
    public Optional<RestaurantDto> findById(UUID id) {
        Optional<RestaurantDocument> foundRestaurant = this.repository.findById(id);
        return foundRestaurant.map(this.mapper::toRestaurantDto);
    }

    @Override
    public Optional<RestaurantDto> findByName(String name) {
        Optional<RestaurantDocument> foundRestaurant = this.repository.findByName(name);
        return foundRestaurant.map(this.mapper::toRestaurantDto);
    }

    @Override
    public void deleteById(UUID id) {
        this.repository.deleteById(id);
    }
}
