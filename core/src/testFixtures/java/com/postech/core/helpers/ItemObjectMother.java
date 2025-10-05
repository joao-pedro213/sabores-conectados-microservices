package com.postech.core.helpers;

import com.postech.core.item.domain.entity.ItemEntity;
import com.postech.core.item.dto.ItemDto;
import com.postech.core.item.dto.NewItemDto;
import com.postech.core.item.dto.UpdateItemDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class ItemObjectMother {

    public static ItemEntity buildItemEntity(Map<String, Object> sampleData) {
        return ItemEntity
                .builder()
                .id((UUID) sampleData.get("id"))
                .restaurantId((UUID) sampleData.get("restaurantId"))
                .name((String) sampleData.get("name"))
                .description((String) sampleData.get("description"))
                .price((BigDecimal) sampleData.get("price"))
                .availableOnlyAtRestaurant((Boolean) sampleData.get("availableOnlyAtRestaurant"))
                .photoPath((String) sampleData.get("photoPath"))
                .lastUpdated((LocalDateTime) sampleData.get("lastUpdated"))
                .build();
    }

    public static ItemDto buildItemDto(Map<String, Object> sampleData) {
        return ItemDto
                .builder()
                .id((UUID) sampleData.get("id"))
                .restaurantId((UUID) sampleData.get("restaurantId"))
                .name((String) sampleData.get("name"))
                .description((String) sampleData.get("description"))
                .price((BigDecimal) sampleData.get("price"))
                .availableOnlyAtRestaurant((Boolean) sampleData.get("availableOnlyAtRestaurant"))
                .photoPath((String) sampleData.get("photoPath"))
                .lastUpdated((LocalDateTime) sampleData.get("lastUpdated"))
                .build();
    }

    public static NewItemDto buildNewItemDto(Map<String, Object> sampleData) {
        return NewItemDto
                .builder()
                .restaurantId((UUID) sampleData.get("restaurantId"))
                .name((String) sampleData.get("name"))
                .description((String) sampleData.get("description"))
                .price((BigDecimal) sampleData.get("price"))
                .availableOnlyAtRestaurant((Boolean) sampleData.get("availableOnlyAtRestaurant"))
                .photoPath((String) sampleData.get("photoPath"))
                .build();
    }

    public static UpdateItemDto buildUpdateItemDto(Map<String, Object> sampleData) {
        return UpdateItemDto
                .builder()
                .name((String) sampleData.get("name"))
                .description((String) sampleData.get("description"))
                .price((BigDecimal) sampleData.get("price"))
                .availableOnlyAtRestaurant((Boolean) sampleData.get("availableOnlyAtRestaurant"))
                .photoPath((String) sampleData.get("photoPath"))
                .build();
    }
}
