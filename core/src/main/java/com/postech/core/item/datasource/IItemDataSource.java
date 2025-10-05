package com.postech.core.item.datasource;


import com.postech.core.item.dto.ItemDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IItemDataSource {
    ItemDto save(ItemDto itemDto);

    Optional<ItemDto> findById(UUID id);

    List<ItemDto> findAllByRestaurantId(UUID restaurantId);

    void deleteById(UUID id);
}
