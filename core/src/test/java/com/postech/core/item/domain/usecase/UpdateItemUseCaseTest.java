package com.postech.core.item.domain.usecase;

import com.postech.core.common.exception.EntityNotFoundException;
import com.postech.core.item.domain.entity.ItemEntity;
import com.postech.core.item.gateway.ItemGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateItemUseCaseTest {

    @Mock
    private ItemGateway itemGateway;

    @InjectMocks
    private UpdateItemUseCase useCase;

    @Test
    @DisplayName("Should update an Item if it exists in the database")
    void shouldUpdateItem() {
        // Given
        final UUID itemId = UUID.randomUUID();
        final String newName = "New Name";
        final String newDescription = "New Description";
        final BigDecimal newPrice = BigDecimal.TEN;
        final Boolean newAvailableOnlyAtRestaurant = false;
        final String newPhotoPath = "/new/photo.jpg";
        final ItemEntity foundItem = ItemEntity.builder().build();
        when(this.itemGateway.findById(itemId)).thenReturn(Optional.of(foundItem));
        final ItemEntity updatedItem = ItemEntity.builder()
                .id(itemId)
                .name(newName)
                .description(newDescription)
                .price(newPrice)
                .availableOnlyAtRestaurant(newAvailableOnlyAtRestaurant)
                .photoPath(newPhotoPath)
                .build();
        when(this.itemGateway.save(any(ItemEntity.class))).thenReturn(updatedItem);

        // When
        final ItemEntity result = this.useCase.execute(itemId, newName, newDescription, newPrice, newAvailableOnlyAtRestaurant, newPhotoPath);

        // Then
        assertThat(result).isNotNull().isEqualTo(updatedItem);
        final ArgumentCaptor<ItemEntity> captor = ArgumentCaptor.forClass(ItemEntity.class);
        verify(this.itemGateway, times(1)).save(captor.capture());
        final ItemEntity capturedItem = captor.getValue();
        assertThat(capturedItem.getName()).isEqualTo(newName);
        assertThat(capturedItem.getDescription()).isEqualTo(newDescription);
        assertThat(capturedItem.getPrice()).isEqualTo(newPrice);
        assertThat(capturedItem.isAvailableOnlyAtRestaurant()).isEqualTo(newAvailableOnlyAtRestaurant);
        assertThat(capturedItem.getPhotoPath()).isEqualTo(newPhotoPath);
    }

    @Test
    @DisplayName("Should throw a EntityNotFoundException when the item is not found in the database")
    void shouldThrowEntityNotFoundException() {
        // Given
        final UUID itemId = UUID.randomUUID();
        when(this.itemGateway.findById(itemId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> this.useCase.execute(itemId, "Name", "Description", BigDecimal.ONE, true, "/photo.jpg"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("The Item with the provided identifier was not found");
    }
}
