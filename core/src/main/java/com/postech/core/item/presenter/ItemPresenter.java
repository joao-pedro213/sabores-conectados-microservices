package com.postech.core.item.presenter;

import com.postech.core.item.domain.entity.ItemEntity;
import com.postech.core.item.dto.ItemDto;

public class ItemPresenter {
    public static ItemPresenter build() {
        return new ItemPresenter();
    }

    public ItemDto toDto(ItemEntity itemEntity) {
        return ItemDto
                .builder()
                .id(itemEntity.getId())
                .restaurantId(itemEntity.getRestaurantId())
                .name(itemEntity.getName())
                .description(itemEntity.getDescription())
                .price(itemEntity.getPrice())
                .availableOnlyAtRestaurant(itemEntity.isAvailableOnlyAtRestaurant())
                .photoPath(itemEntity.getPhotoPath())
                .lastUpdated(itemEntity.getLastUpdated())
                .build();
    }
}
