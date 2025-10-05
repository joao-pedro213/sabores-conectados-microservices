package com.postech.core.item.gateway;

import com.postech.core.item.datasource.IItemDataSource;
import com.postech.core.item.domain.entity.ItemEntity;
import com.postech.core.item.dto.ItemDto;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class ItemGateway {
    private IItemDataSource dataSource;

    public static ItemGateway build(IItemDataSource dataSource) {
        return new ItemGateway(dataSource);
    }

    public ItemEntity save(ItemEntity itemEntity) {
        ItemDto itemDto = this.dataSource.save(this.toItemDto(itemEntity));
        return this.toItemEntity(itemDto);
    }

    public Optional<ItemEntity> findById(UUID id) {
        Optional<ItemDto> foundItem = this.dataSource.findById(id);
        return foundItem.map(this::toItemEntity);
    }

    public List<ItemEntity> findAllByRestaurantId(UUID restaurantId) {
        return this.dataSource.findAllByRestaurantId(restaurantId).stream().map(this::toItemEntity).toList();
    }

    public void deleteById(UUID id) {
        this.dataSource.deleteById(id);
    }

    private ItemDto toItemDto(ItemEntity itemEntity) {
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

    private ItemEntity toItemEntity(ItemDto itemDto) {
        return ItemEntity
                .builder()
                .id(itemDto.getId())
                .restaurantId(itemDto.getRestaurantId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .price(itemDto.getPrice())
                .availableOnlyAtRestaurant(itemDto.getAvailableOnlyAtRestaurant())
                .photoPath(itemDto.getPhotoPath())
                .lastUpdated(itemDto.getLastUpdated())
                .build();
    }
}
