package com.postech.core.restaurant.domain.usecase;

import com.postech.core.restaurant.gateway.RestaurantGateway;
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
class DeleteRestaurantByIdUseCaseTest {
    @Mock
    private RestaurantGateway mockRestaurantGateway;

    @InjectMocks
    private DeleteRestaurantByIdUseCase useCase;

    private static final UUID ID = UUID.randomUUID();

    @Test
    @DisplayName("should delete a Restaurant from the database")
    void shouldDeleteRestaurantById() {
        // When
        this.useCase.execute(ID);

        // Then
        verify(this.mockRestaurantGateway, times(1)).deleteById(ID);
    }
}
