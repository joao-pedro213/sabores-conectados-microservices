package com.postech.core.item.controller;

import com.postech.core.item.datasource.IItemDataSource;
import com.postech.core.item.domain.entity.ItemEntity;
import com.postech.core.item.domain.usecase.CreateItemUseCase;
import com.postech.core.item.domain.usecase.DeleteItemByIdUseCase;
import com.postech.core.item.domain.usecase.RetrieveItemsByRestaurantIdUseCase;
import com.postech.core.item.domain.usecase.UpdateItemUseCase;
import com.postech.core.item.dto.ItemDto;
import com.postech.core.item.dto.NewItemDto;
import com.postech.core.item.dto.UpdateItemDto;
import com.postech.core.item.gateway.ItemGateway;
import com.postech.core.item.presenter.ItemPresenter;

import java.util.List;
import java.util.UUID;

public class ItemController {
    private final ItemGateway itemGateway;

    public ItemController(IItemDataSource itemDataSource) {
        this.itemGateway = ItemGateway.build(itemDataSource);
    }

    public static ItemController build(IItemDataSource itemDataSource) {
        return new ItemController(itemDataSource);
    }

    public ItemDto createItem(NewItemDto newItemDto) {
        ItemEntity createdItem = CreateItemUseCase.build(this.itemGateway).execute(this.toDomain(newItemDto));
        return ItemPresenter.build().toDto(createdItem);
    }

    public List<ItemDto> retrieveItemsByRestaurantId(UUID restaurantId) {
        List<ItemEntity> foundItems = RetrieveItemsByRestaurantIdUseCase.build(this.itemGateway).execute(restaurantId);
        return foundItems.stream().map(ItemPresenter.build()::toDto).toList();
    }

    public ItemDto updateItem(UUID id, UpdateItemDto updateItemDto) {
        ItemEntity updatedItem = UpdateItemUseCase
                .build(this.itemGateway)
                .execute(
                        id,
                        updateItemDto.getName(),
                        updateItemDto.getDescription(),
                        updateItemDto.getPrice(),
                        updateItemDto.getAvailableOnlyAtRestaurant(),
                        updateItemDto.getPhotoPath());
        return ItemPresenter.build().toDto(updatedItem);
    }

    public void deleteItemById(UUID id) {
        DeleteItemByIdUseCase.build(this.itemGateway).execute(id);
    }

    private ItemEntity toDomain(NewItemDto newItemDto) {
        return ItemEntity
                .builder()
                .restaurantId(newItemDto.getRestaurantId())
                .name(newItemDto.getName())
                .description(newItemDto.getDescription())
                .price(newItemDto.getPrice())
                .availableOnlyAtRestaurant(newItemDto.getAvailableOnlyAtRestaurant())
                .photoPath(newItemDto.getPhotoPath())
                .build();
    }
}
