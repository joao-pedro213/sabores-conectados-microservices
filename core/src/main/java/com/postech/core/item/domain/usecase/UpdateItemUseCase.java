package com.postech.core.item.domain.usecase;

import com.postech.core.common.exception.EntityNotFoundException;
import com.postech.core.item.domain.entity.ItemEntity;
import com.postech.core.item.gateway.ItemGateway;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
public class UpdateItemUseCase {
    private ItemGateway itemGateway;

    public static UpdateItemUseCase build(ItemGateway itemGateway) {
        return new UpdateItemUseCase(itemGateway);
    }

    public ItemEntity execute(
            UUID id, String name, String description,
            BigDecimal price, Boolean availableOnlyAtRestaurant, String photoPath) {
        ItemEntity foundItem = this.itemGateway
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item"));
        ItemEntity itemWithUpdates = foundItem
                .toBuilder()
                .name(name)
                .description(description)
                .price(price)
                .availableOnlyAtRestaurant(availableOnlyAtRestaurant)
                .photoPath(photoPath)
                .build();
        return this.itemGateway.save(itemWithUpdates);
    }
}
