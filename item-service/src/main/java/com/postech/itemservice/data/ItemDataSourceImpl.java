package com.postech.itemservice.data;

import com.postech.core.item.datasource.IItemDataSource;
import com.postech.core.item.dto.ItemDto;
import com.postech.itemservice.data.document.ItemDocument;
import com.postech.itemservice.data.repository.IItemRepository;
import com.postech.itemservice.mapper.IItemMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ItemDataSourceImpl implements IItemDataSource {
    private final IItemRepository repository;
    private final IItemMapper mapper;

    @Override
    public ItemDto save(ItemDto itemDto) {
        ItemDocument itemToSave = this.mapper.toItemDocument(itemDto);
        ItemDocument savedItem = this.repository.save(itemToSave);
        return this.mapper.toItemDto(savedItem);
    }

    @Override
    public Optional<ItemDto> findById(UUID id) {
        return this.repository.findById(id).map(this.mapper::toItemDto);
    }

    @Override
    public List<ItemDto> findAllByRestaurantId(UUID restaurantId) {
        return this.repository
                .findAllByRestaurantId(restaurantId)
                .stream()
                .map(this.mapper::toItemDto)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        this.repository.deleteById(id);
    }
}
