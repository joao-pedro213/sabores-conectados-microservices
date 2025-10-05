package com.postech.core.item.domain.usecase;

import com.postech.core.item.gateway.ItemGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteItemByIdUseCaseTest {

    @Mock
    private ItemGateway itemGateway;

    @InjectMocks
    private DeleteItemByIdUseCase useCase;

    private static final UUID ID = UUID.randomUUID();

    @Test
    @DisplayName("should delete an Item from the database")
    void shouldDeleteItemById() {
        // When
        useCase.execute(ID);

        // Then
        verify(itemGateway, times(1)).deleteById(ID);
    }
}
