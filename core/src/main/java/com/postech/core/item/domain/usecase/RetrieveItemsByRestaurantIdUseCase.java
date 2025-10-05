package com.postech.core.item.domain.usecase;

import com.postech.core.item.domain.entity.ItemEntity;
import com.postech.core.item.gateway.ItemGateway;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class RetrieveItemsByRestaurantIdUseCase {
    private final ItemGateway itemGateway;

    public static RetrieveItemsByRestaurantIdUseCase build(ItemGateway itemGateway) {
        return new RetrieveItemsByRestaurantIdUseCase(itemGateway);
    }

    public List<ItemEntity> execute(UUID restaurantId) {
        return this.itemGateway.findAllByRestaurantId(restaurantId);
    }
}
