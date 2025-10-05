package com.postech.core.item.controller;

import com.postech.core.helpers.ItemObjectMother;
import com.postech.core.item.datasource.IItemDataSource;
import com.postech.core.item.domain.entity.ItemEntity;
import com.postech.core.item.domain.usecase.CreateItemUseCase;
import com.postech.core.item.domain.usecase.DeleteItemByIdUseCase;
import com.postech.core.item.domain.usecase.RetrieveItemsByRestaurantIdUseCase;
import com.postech.core.item.domain.usecase.UpdateItemUseCase;
import com.postech.core.item.dto.ItemDto;
import com.postech.core.item.dto.NewItemDto;
import com.postech.core.item.dto.UpdateItemDto;
import com.postech.core.item.gateway.ItemGateway;
import com.postech.core.item.presenter.ItemPresenter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private IItemDataSource mockItemDataSource;

    @InjectMocks
    private ItemController controller;

    @Mock
    private ItemPresenter mockItemPresenter;

    private MockedStatic<ItemPresenter> mockedStaticItemPresenter;

    @BeforeEach
    void setUp() {
        mockedStaticItemPresenter = mockStatic(ItemPresenter.class);
        mockedStaticItemPresenter.when(ItemPresenter::build).thenReturn(mockItemPresenter);
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
    void shouldCreateItem() {
        // Given
        final Map<String, Object> itemSampleData = getSampleItemData();
        final NewItemDto newItemDto = ItemObjectMother.buildNewItemDto(itemSampleData);
        final ItemEntity createdItemEntity = ItemObjectMother.buildItemEntity(itemSampleData);
        final ItemDto createdItemDto = ItemDto.builder().build();
        final CreateItemUseCase mockCreateItemUseCase = mock(CreateItemUseCase.class);
        when(mockCreateItemUseCase.execute(any(ItemEntity.class))).thenReturn(createdItemEntity);
        when(this.mockItemPresenter.toDto(createdItemEntity)).thenReturn(createdItemDto);
        try (MockedStatic<CreateItemUseCase> mockedStaticCreateItemUseCase = mockStatic(CreateItemUseCase.class)) {
            mockedStaticCreateItemUseCase.when(() -> CreateItemUseCase.build(any(ItemGateway.class))).thenReturn(mockCreateItemUseCase);

            // When
            final ItemDto itemDto = this.controller.createItem(newItemDto);

            // Then
            final ArgumentCaptor<ItemEntity> captor = ArgumentCaptor.forClass(ItemEntity.class);
            verify(mockCreateItemUseCase, times(1)).execute(captor.capture());
            final ItemEntity capturedItemEntity = captor.getValue();
            final ItemEntity expectedItemEntity = createdItemEntity.toBuilder().id(null).lastUpdated(null).build();
            assertThat(capturedItemEntity).usingRecursiveComparison().isEqualTo(expectedItemEntity);
            assertThat(itemDto).isNotNull().isEqualTo(createdItemDto);
        }
    }

    @Test
    void shouldRetrieveItemsByRestaurantId() {
        // Given
        final Map<String, Object> itemSampleData = getSampleItemData();
        final UUID restaurantId = (UUID) itemSampleData.get("restaurantId");
        final List<ItemEntity> foundItems = List.of(ItemObjectMother.buildItemEntity(itemSampleData));
        final RetrieveItemsByRestaurantIdUseCase mockRetrieveItemsUseCase = mock(RetrieveItemsByRestaurantIdUseCase.class);
        when(mockRetrieveItemsUseCase.execute(restaurantId)).thenReturn(foundItems);
        final ItemDto foundItemDto = ItemDto.builder().build();
        when(this.mockItemPresenter.toDto(any(ItemEntity.class))).thenReturn(foundItemDto);
        try (MockedStatic<RetrieveItemsByRestaurantIdUseCase> mockedStaticRetrieveItemsUseCase = mockStatic(RetrieveItemsByRestaurantIdUseCase.class)) {
            mockedStaticRetrieveItemsUseCase.when(() -> RetrieveItemsByRestaurantIdUseCase.build(any(ItemGateway.class))).thenReturn(mockRetrieveItemsUseCase);

            // When
            final List<ItemDto> itemDtos = this.controller.retrieveItemsByRestaurantId(restaurantId);

            // Then
            assertThat(itemDtos).isNotNull().hasSize(1);
            assertThat(itemDtos.get(0)).isEqualTo(foundItemDto);
        }
    }

    @Test
    void shouldUpdateItem() {
        // Given
        final Map<String, Object> itemSampleData = getSampleItemData();
        final UUID itemId = (UUID) itemSampleData.get("id");
        final UpdateItemDto updateItemDto = ItemObjectMother.buildUpdateItemDto(itemSampleData);
        final ItemEntity updatedItemEntity = ItemObjectMother.buildItemEntity(itemSampleData);
        final UpdateItemUseCase mockUpdateItemUseCase = mock(UpdateItemUseCase.class);
        when(mockUpdateItemUseCase.execute(any(), any(), any(), any(), any(), any())).thenReturn(updatedItemEntity);
        final ItemDto updatedItemDto = ItemDto.builder().build();
        when(this.mockItemPresenter.toDto(updatedItemEntity)).thenReturn(updatedItemDto);
        try (MockedStatic<UpdateItemUseCase> mockedStaticUpdateItemUseCase = mockStatic(UpdateItemUseCase.class)) {
            mockedStaticUpdateItemUseCase.when(() -> UpdateItemUseCase.build(any(ItemGateway.class))).thenReturn(mockUpdateItemUseCase);

            // When
            final ItemDto itemDto = this.controller.updateItem(itemId, updateItemDto);

            // Then
            assertThat(itemDto).isNotNull().isEqualTo(updatedItemDto);
        }
    }

    @Test
    void shouldDeleteItemById() {
        // Given
        final UUID itemId = UUID.randomUUID();
        final DeleteItemByIdUseCase mockDeleteUseCase = mock(DeleteItemByIdUseCase.class);
        try (MockedStatic<DeleteItemByIdUseCase> mockedStaticDeleteUseCase = mockStatic(DeleteItemByIdUseCase.class)) {
            mockedStaticDeleteUseCase.when(() -> DeleteItemByIdUseCase.build(any(ItemGateway.class))).thenReturn(mockDeleteUseCase);

            // When
            this.controller.deleteItemById(itemId);

            // Then
            verify(mockDeleteUseCase, times(1)).execute(itemId);
        }
    }

    @AfterEach
    void tearDown() {
        if (this.mockedStaticItemPresenter != null) {
            this.mockedStaticItemPresenter.close();
        }
    }
}
