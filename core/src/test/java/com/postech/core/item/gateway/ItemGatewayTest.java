package com.postech.core.item.gateway;

import com.postech.core.helpers.ItemObjectMother;
import com.postech.core.item.datasource.IItemDataSource;
import com.postech.core.item.domain.entity.ItemEntity;
import com.postech.core.item.dto.ItemDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemGatewayTest {

    @Mock
    private IItemDataSource dataSource;

    @InjectMocks
    private ItemGateway gateway;

    private static Map<String, Object> getSampleItemData() {
        return Map.of(
                "id", UUID.randomUUID(),
                "restaurantId", UUID.randomUUID(),
                "name", "Pepperoni Pizza",
                "description", "A delicious Pepperoni Pizza",
                "price", BigDecimal.valueOf(25.0),
                "availableOnlyAtRestaurant", false,
                "photoPath", "/peperoni-pizza.jpg",
                "lastUpdated", LocalDateTime.parse("2025-09-17T00:00:00.000")
        );
    }

    @Test
    void shouldSaveItem() {
        // Given
        final Map<String, Object> itemSampleData = getSampleItemData();
        final ItemEntity itemToSave = ItemObjectMother.buildItemEntity(itemSampleData);
        final ItemDto savedItemDto = ItemObjectMother.buildItemDto(itemSampleData);
        when(this.dataSource.save(any(ItemDto.class))).thenReturn(savedItemDto);

        // When
        final ItemEntity savedItem = this.gateway.save(itemToSave);

        // Then
        final ArgumentCaptor<ItemDto> argument = ArgumentCaptor.forClass(ItemDto.class);
        verify(this.dataSource, times(1)).save(argument.capture());
        final ItemDto capturedItemDto = argument.getValue();
        final ItemDto expectedItemDto = ItemObjectMother.buildItemDto(itemSampleData);
        assertThat(capturedItemDto).usingRecursiveComparison().isEqualTo(expectedItemDto);
        assertThat(savedItem).isNotNull();
        final ItemEntity expectedUpdatedItem = itemToSave.toBuilder().build();
        assertThat(savedItem).usingRecursiveComparison().isEqualTo(expectedUpdatedItem);
    }

    @Test
    void shouldFindItemById() {
        // Given
        final Map<String, Object> itemSampleData = getSampleItemData();
        final ItemDto foundItemDto = ItemObjectMother.buildItemDto(itemSampleData);
        final UUID itemId = (UUID) itemSampleData.get("id");
        when(this.dataSource.findById(itemId)).thenReturn(Optional.of(foundItemDto));

        // When
        Optional<ItemEntity> foundItem = this.gateway.findById(itemId);

        // Then
        assertThat(foundItem).isPresent();
        final ItemEntity expectedFoundItem = ItemObjectMother.buildItemEntity(itemSampleData);
        assertThat(foundItem.get()).usingRecursiveComparison().isEqualTo(expectedFoundItem);
    }

    @Test
    void shouldFindAllByRestaurantId() {
        // Given
        final Map<String, Object> itemSampleData = getSampleItemData();
        final List<ItemDto> foundItemDtos = List.of(ItemObjectMother.buildItemDto(itemSampleData));
        final UUID restaurantId = (UUID) itemSampleData.get("restaurantId");
        when(this.dataSource.findAllByRestaurantId(restaurantId)).thenReturn(foundItemDtos);

        // When
        List<ItemEntity> foundItems = this.gateway.findAllByRestaurantId(restaurantId);

        // Then
        assertThat(foundItems).hasSize(1);
        final ItemEntity expectedFoundItem = ItemObjectMother.buildItemEntity(itemSampleData);
        assertThat(foundItems.getFirst()).usingRecursiveComparison().isEqualTo(expectedFoundItem);
    }

    @Test
    void shouldDeleteItemById() {
        // Given
        final Map<String, Object> itemSampleData = getSampleItemData();
        final UUID itemId = (UUID) itemSampleData.get("id");

        // When
        this.gateway.deleteById(itemId);

        // Then
        verify(this.dataSource, times(1)).deleteById(itemId);
    }
}
