package com.postech.core.item.domain.usecase;

import com.postech.core.item.domain.entity.ItemEntity;
import com.postech.core.item.gateway.ItemGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateItemUseCaseTest {

    @Mock
    private ItemGateway mockItemGateway;

    @InjectMocks
    private CreateItemUseCase useCase;

    @Test
    @DisplayName("Should create a new Item")
    void shouldCreateItem() {
        // Given
        final ItemEntity newItemEntity = ItemEntity.builder().build();
        final ItemEntity createdItemEntity = newItemEntity.toBuilder().build();
        when(this.mockItemGateway.save(newItemEntity)).thenReturn(createdItemEntity);

        // When
        final ItemEntity itemEntity = this.useCase.execute(newItemEntity);

        // Then
        assertThat(itemEntity).isNotNull().isEqualTo(createdItemEntity);
    }
}
