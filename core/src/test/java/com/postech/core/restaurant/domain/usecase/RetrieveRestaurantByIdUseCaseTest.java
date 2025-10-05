package com.postech.core.restaurant.domain.usecase;

import com.postech.core.common.exception.EntityNotFoundException;
import com.postech.core.restaurant.domain.entity.RestaurantEntity;
import com.postech.core.restaurant.gateway.RestaurantGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RetrieveRestaurantByIdUseCaseTest {
    @Mock
    private RestaurantGateway mockRestaurantGateway;

    @InjectMocks
    private RetrieveRestaurantByIdUseCase useCase;

    private static final UUID RESTAURANT_ID = UUID.randomUUID();

    @Test
    @DisplayName("Should find a Restaurant if it exists in the database")
    void shouldFindRestaurantById() {
        // Given
        final RestaurantEntity foundRestaurantEntity = RestaurantEntity.builder().build();
        when(this.mockRestaurantGateway.findById(RESTAURANT_ID)).thenReturn(Optional.of(foundRestaurantEntity));

        // When
        final RestaurantEntity restaurantEntity = this.useCase.execute(RESTAURANT_ID);

        // Then
        assertThat(restaurantEntity).isNotNull().isEqualTo(foundRestaurantEntity);
    }

    @Test
    @DisplayName("should throw EntityNotFoundException when the restaurant is not found in the database")
    void shouldThrowEntityNotFoundException() {
        // Given
        when(this.mockRestaurantGateway.findById(RESTAURANT_ID)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> this.useCase.execute(RESTAURANT_ID)).isInstanceOf(EntityNotFoundException.class);
    }
}
