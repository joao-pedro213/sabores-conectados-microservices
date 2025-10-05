package com.postech.core.restaurant.domain.usecase;

import com.postech.core.common.exception.EntityAlreadyExistsException;
import com.postech.core.restaurant.domain.entity.RestaurantEntity;
import com.postech.core.restaurant.gateway.RestaurantGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateRestaurantUseCaseTest {
    @Mock
    private RestaurantGateway mockRestaurantGateway;

    @InjectMocks
    private CreateRestaurantUseCase useCase;

    @Test
    @DisplayName("Should create a new Restaurant if it doesn't exist in the database yet")
    void shouldCreateRestaurant() {
        // Given
        final String restaurantName = "test-restaurant";
        final RestaurantEntity newRestaurantEntity = RestaurantEntity.builder().name(restaurantName).build();
        final RestaurantEntity createdRestaurantEntity = newRestaurantEntity.toBuilder().build();
        when(this.mockRestaurantGateway.findByName(newRestaurantEntity.getName())).thenReturn(Optional.empty());
        when(this.mockRestaurantGateway.save(newRestaurantEntity)).thenReturn(createdRestaurantEntity);

        // When
        final RestaurantEntity restaurantEntity = this.useCase.execute(newRestaurantEntity);

        // Then
        assertThat(restaurantEntity).isNotNull().isEqualTo(createdRestaurantEntity);
    }

    @Test
    @DisplayName("should throw a EntityAlreadyExistsException when the new restaurant is found in the database before its creation")
    void shouldThrowEntityAlreadyExist() {
        // Given
        final String restaurantName = "test-restaurant";
        final RestaurantEntity newRestaurantEntity = RestaurantEntity.builder().name(restaurantName).build();
        when(this.mockRestaurantGateway.findByName(newRestaurantEntity.getName())).thenReturn(Optional.of(newRestaurantEntity));

        // When & Then
        assertThatThrownBy(() -> this.useCase.execute(newRestaurantEntity)).isInstanceOf(EntityAlreadyExistsException.class);
    }
}
