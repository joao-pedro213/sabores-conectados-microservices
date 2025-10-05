package com.postech.core.restaurant.domain.usecase;

import com.postech.core.common.exception.EntityNotFoundException;
import com.postech.core.restaurant.domain.entity.RestaurantEntity;
import com.postech.core.restaurant.gateway.RestaurantGateway;
import com.postech.core.restaurant.valueobject.DailySchedule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateRestaurantUseCaseTest {
    @Mock
    private RestaurantGateway mockRestaurantGateway;

    @InjectMocks
    private UpdateRestaurantUseCase useCase;

    @Test
    @DisplayName("should update a Restaurant if it exists in the database")
    void shouldUpdateRestaurant() {
        // Given
        final UUID restaurantId = UUID.randomUUID();
        final String newAddress = "321 AI Avenue, Tech City";
        final Map<DayOfWeek, DailySchedule> newBusinessHours = Map.of(
                DayOfWeek.FRIDAY,
                DailySchedule.builder().openingTime(LocalTime.parse("11:00")).closingTime(LocalTime.parse("23:59")).build(),
                DayOfWeek.SATURDAY,
                DailySchedule.builder().openingTime(LocalTime.parse("11:00")).closingTime(LocalTime.parse("23:00")).build()
        );
        final RestaurantEntity restaurantEntityWithUpdates = RestaurantEntity.builder().address(newAddress).businessHours(newBusinessHours).build();
        final RestaurantEntity foundRestaurantEntity = RestaurantEntity.builder().build();
        when(this.mockRestaurantGateway.findById(restaurantId)).thenReturn(Optional.of(foundRestaurantEntity));
        final RestaurantEntity updatedRestaurantEntity = restaurantEntityWithUpdates.toBuilder().build();
        when(this.mockRestaurantGateway.save(any(RestaurantEntity.class))).thenReturn(updatedRestaurantEntity);

        // When
        final RestaurantEntity restaurantEntity = this.useCase
                .execute(restaurantId, Collections.emptyList(), newAddress, newBusinessHours);

        // Then
        assertThat(restaurantEntity).isNotNull().isEqualTo(updatedRestaurantEntity);
        final ArgumentCaptor<RestaurantEntity> captor = ArgumentCaptor.forClass(RestaurantEntity.class);
        verify(this.mockRestaurantGateway, times(1)).save(captor.capture());
        final RestaurantEntity capturedRestaurantEntity = captor.getValue();
        assertThat(capturedRestaurantEntity.getManagerIds()).isEmpty();
        assertThat(capturedRestaurantEntity.getAddress()).isNotNull().isEqualTo(newAddress);
        assertThat(capturedRestaurantEntity.getBusinessHours()).isNotNull().isEqualTo(newBusinessHours);
    }

    @Test
    @DisplayName("should throw a EntityNotFoundException when the restaurant is not found in the database")
    void shouldThrowEntityNotFoundException() {
        // Given
        final UUID restaurantId = UUID.randomUUID();
        final String newAddress = "321 AI Avenue, Tech City";
        when(this.mockRestaurantGateway.findById(restaurantId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() ->
                this.useCase.execute(restaurantId, Collections.emptyList(), newAddress, null))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
