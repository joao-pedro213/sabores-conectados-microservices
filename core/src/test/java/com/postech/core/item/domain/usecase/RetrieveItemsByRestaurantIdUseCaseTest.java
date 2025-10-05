package com.postech.core.item.domain.usecase;

import com.postech.core.item.domain.entity.ItemEntity;
import com.postech.core.item.gateway.ItemGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetrieveItemsByRestaurantIdUseCaseTest {

    @Mock
    private ItemGateway itemGateway;

    @InjectMocks
    private RetrieveItemsByRestaurantIdUseCase useCase;

    private static final UUID RESTAURANT_ID = UUID.randomUUID();

    @Test
    @DisplayName("Should return a list of items for a given restaurant")
    void shouldReturnListOfItemsForRestaurant() {
        // Given
        List<ItemEntity> items = List.of(ItemEntity.builder().build());
        when(itemGateway.findAllByRestaurantId(RESTAURANT_ID)).thenReturn(items);

        // When
        List<ItemEntity> result = useCase.execute(RESTAURANT_ID);

        // Then
        assertThat(result).isNotNull().isEqualTo(items);
    }

    @Test
    @DisplayName("Should return an empty list when no items are found for a given restaurant")
    void shouldReturnEmptyListWhenNoItemsFound() {
        // Given
        when(itemGateway.findAllByRestaurantId(RESTAURANT_ID)).thenReturn(Collections.emptyList());

        // When
        List<ItemEntity> result = useCase.execute(RESTAURANT_ID);

        // Then
        assertThat(result).isNotNull().isEmpty();
    }
}
