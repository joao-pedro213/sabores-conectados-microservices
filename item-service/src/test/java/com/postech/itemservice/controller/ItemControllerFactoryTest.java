package com.postech.itemservice.controller;

import com.postech.core.item.controller.ItemController;
import com.postech.itemservice.data.ItemDataSourceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ItemControllerFactoryTest {

    @Mock
    private ItemDataSourceImpl itemDataSourceImpl;

    @InjectMocks
    private ItemControllerFactory itemControllerFactory;

    @Test
    void shouldBuildItemController() {
        // When
        ItemController itemController = this.itemControllerFactory.build();

        // Then
        assertThat(itemController).isNotNull();
    }
}
