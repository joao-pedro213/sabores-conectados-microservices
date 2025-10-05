package com.postech.itemservice.data;

import com.postech.core.item.dto.ItemDto;
import com.postech.itemservice.data.document.ItemDocument;
import com.postech.itemservice.data.repository.IItemRepository;
import com.postech.itemservice.mapper.IItemMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemDataSourceImplTest {
    @Mock
    private IItemRepository mockRepository;

    @Mock
    private IItemMapper mockMapper;

    @InjectMocks
    private ItemDataSourceImpl dataSource;

    @Test
    void shouldSaveItem() {
        // Given
        final ItemDto itemToSaveDto = ItemDto.builder().build();
        final ItemDocument itemToSave = ItemDocument.builder().build();
        final ItemDto expectedSavedItemDto = ItemDto.builder().build();
        when(this.mockMapper.toItemDocument(itemToSaveDto)).thenReturn(itemToSave);
        when(this.mockRepository.save(itemToSave)).thenReturn(itemToSave);
        when(this.mockMapper.toItemDto(itemToSave)).thenReturn(expectedSavedItemDto);

        // When
        final ItemDto savedItemDto = this.dataSource.save(itemToSaveDto);

        // Then
        assertThat(savedItemDto).isNotNull().isEqualTo(expectedSavedItemDto);
    }

    @Test
    void shouldFindItemById() {
        // Given
        final UUID itemId = UUID.randomUUID();
        final ItemDocument foundItem = ItemDocument.builder().build();
        when(this.mockRepository.findById(itemId)).thenReturn(Optional.of(foundItem));
        final ItemDto mappedItemDto = ItemDto.builder().build();
        when(this.mockMapper.toItemDto(foundItem)).thenReturn(mappedItemDto);

        // When
        Optional<ItemDto> foundItemDto = this.dataSource.findById(itemId);

        // Then
        assertThat(foundItemDto).isPresent().contains(mappedItemDto);
    }

    @Test
    void shouldFindAllByRestaurantId() {
        // Given
        final UUID restaurantId = UUID.randomUUID();
        final ItemDocument foundItem = ItemDocument.builder().build();
        final List<ItemDocument> foundItems = List.of(foundItem);
        when(this.mockRepository.findAllByRestaurantId(restaurantId)).thenReturn(foundItems);
        final ItemDto mappedItemDto = ItemDto.builder().build();
        when(this.mockMapper.toItemDto(foundItem)).thenReturn(mappedItemDto);

        // When
        List<ItemDto> foundItemDtos = this.dataSource.findAllByRestaurantId(restaurantId);

        // Then
        assertThat(foundItemDtos).isNotNull().hasSize(1);
        assertThat(foundItemDtos.getFirst()).isEqualTo(mappedItemDto);
    }

    @Test
    void shouldDeleteItemById() {
        // Given
        final UUID itemId = UUID.randomUUID();

        // When
        this.dataSource.deleteById(itemId);

        // Then
        verify(this.mockRepository, times(1)).deleteById(itemId);
    }
}
