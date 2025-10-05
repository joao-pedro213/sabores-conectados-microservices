package com.postech.restaurantservice.data.mapper;

import com.postech.core.restaurant.dto.NewRestaurantDto;
import com.postech.core.restaurant.dto.RestaurantDto;
import com.postech.core.restaurant.dto.UpdateRestaurantDto;
import com.postech.restaurantservice.data.document.RestaurantDocument;
import com.postech.restaurantservice.dto.NewRestaurantRequestDto;
import com.postech.restaurantservice.dto.RestaurantResponseDto;
import com.postech.restaurantservice.dto.UpdateRestaurantRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IRestaurantMapper {
    RestaurantDocument toRestaurantDocument(RestaurantDto restaurantDto);

    RestaurantDto toRestaurantDto(RestaurantDocument restaurantDocument);

    NewRestaurantDto toNewRestaurantDto(NewRestaurantRequestDto newRestaurantRequestDto);

    UpdateRestaurantDto toUpdateRestaurantDto(UpdateRestaurantRequestDto updateRestaurantRequestDto);

    RestaurantResponseDto toRestaurantResponseDto(RestaurantDto restaurantDto);
}
