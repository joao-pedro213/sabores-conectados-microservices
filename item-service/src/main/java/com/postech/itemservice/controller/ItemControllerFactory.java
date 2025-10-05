package com.postech.itemservice.controller;

import com.postech.core.item.controller.ItemController;
import com.postech.itemservice.data.ItemDataSourceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ItemControllerFactory {
    private ItemDataSourceImpl dataSource;

    public ItemController build() {
        return ItemController.build(this.dataSource);
    }
}
