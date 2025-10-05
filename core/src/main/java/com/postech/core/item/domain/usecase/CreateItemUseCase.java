package com.postech.core.item.domain.usecase;

import com.postech.core.item.domain.entity.ItemEntity;
import com.postech.core.item.gateway.ItemGateway;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
public class CreateItemUseCase {
    private ItemGateway itemGateway;

    public static CreateItemUseCase build(ItemGateway itemGateway) {
        return new CreateItemUseCase(itemGateway);
    }

    public ItemEntity execute(ItemEntity itemEntity) {
        return this.itemGateway.save(itemEntity);
    }
}
