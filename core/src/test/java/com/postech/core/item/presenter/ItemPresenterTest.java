package com.postech.core.item.presenter;

import com.postech.core.helpers.ItemObjectMother;
import com.postech.core.item.domain.entity.ItemEntity;
import com.postech.core.item.dto.ItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ItemPresenterTest {
    private ItemPresenter presenter;

    @BeforeEach
    void setUp() {
        this.presenter = ItemPresenter.build();
    }

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
    void shouldMapDomainToDto() {
        // Given
        final Map<String, Object> itemSampleData = getSampleItemData();
        final ItemEntity itemEntity = ItemObjectMother.buildItemEntity(itemSampleData);

        // When
        final ItemDto itemDto = this.presenter.toDto(itemEntity);

        // Then
        final ItemDto expectedItemDto = ItemObjectMother.buildItemDto(itemSampleData);
        assertThat(itemDto).usingRecursiveComparison().isEqualTo(expectedItemDto);
    }
}
