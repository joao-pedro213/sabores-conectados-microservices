package com.postech.core.item.domain.usecase;

import com.postech.core.item.gateway.ItemGateway;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class DeleteItemByIdUseCase {
    private final ItemGateway itemGateway;

    public static DeleteItemByIdUseCase build(ItemGateway itemGateway) {
        return new DeleteItemByIdUseCase(itemGateway);
    }

    public void execute(UUID id) {
        this.itemGateway.deleteById(id);
    }
}
