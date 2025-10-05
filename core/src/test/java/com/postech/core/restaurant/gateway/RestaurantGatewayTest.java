package com.postech.core.restaurant.gateway;

import com.postech.core.helpers.RestaurantObjectMother;
import com.postech.core.restaurant.datasource.IRestaurantDataSource;
import com.postech.core.restaurant.domain.entity.RestaurantEntity;
import com.postech.core.restaurant.dto.RestaurantDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantGatewayTest {
    @Mock
    private IRestaurantDataSource dataSource;

    @InjectMocks
    private RestaurantGateway gateway;

    private static Map<String, Object> getSampleRestaurantData() {
        return Map.of(
                "id", UUID.randomUUID(),
                "ownerId", UUID.randomUUID(),
                "name", "test-restaurant",
                "address", "test address 123",
                "cuisineType", "MEXICAN",
                "businessHours", Map.of(DayOfWeek.FRIDAY, Map.of("openingTime", "10:00", "closingTime", "23:00")),
                "lastUpdated", LocalDateTime.parse("2025-09-17T00:00:00.000")
        );
    }

    @Test
    void shouldSaveRestaurant() {
        // Given
        final Map<String, Object> restaurantSampleData = getSampleRestaurantData();
        final RestaurantEntity restaurantToSave = RestaurantObjectMother.buildRestaurantEntity(restaurantSampleData);
        final RestaurantDto savedRestaurantDto = RestaurantObjectMother.buildRestaurantDto(restaurantSampleData);
        when(this.dataSource.save(any(RestaurantDto.class))).thenReturn(savedRestaurantDto);

        // When
        final RestaurantEntity savedRestaurant = this.gateway.save(restaurantToSave);

        // Then
        final ArgumentCaptor<RestaurantDto> argument = ArgumentCaptor.forClass(RestaurantDto.class);
        verify(this.dataSource, times(1)).save(argument.capture());
        final RestaurantDto capturedRestaurantDto = argument.getValue();
        final RestaurantDto expectedRestaurantDto = RestaurantObjectMother.buildRestaurantDto(restaurantSampleData);
        assertThat(capturedRestaurantDto).usingRecursiveComparison().isEqualTo(expectedRestaurantDto);
        assertThat(savedRestaurant).isNotNull();
        final RestaurantEntity expectedUpdatedRestaurant = restaurantToSave.toBuilder().build();
        assertThat(savedRestaurant).usingRecursiveComparison().isEqualTo(expectedUpdatedRestaurant);
    }

    @Test
    void shouldFindRestaurantById() {
        // Given
        final Map<String, Object> restaurantSampleData = getSampleRestaurantData();
        final RestaurantDto foundRestaurantDto = RestaurantObjectMother.buildRestaurantDto(restaurantSampleData);
        when(this.dataSource.findById(UUID.fromString(restaurantSampleData.get("id").toString()))).thenReturn(Optional.of(foundRestaurantDto));

        // When
        Optional<RestaurantEntity> foundRestaurant = this.gateway.findById(UUID.fromString(restaurantSampleData.get("id").toString()));

        // Then
        assertThat(foundRestaurant).isPresent();
        final RestaurantEntity expectedFoundRestaurant = RestaurantObjectMother.buildRestaurantEntity(restaurantSampleData);
        assertThat(foundRestaurant.get()).usingRecursiveComparison().isEqualTo(expectedFoundRestaurant);
    }

    @Test
    void shouldFindRestaurantByName() {
        // Given
        final Map<String, Object> restaurantSampleData = getSampleRestaurantData();
        final String name = restaurantSampleData.get("name").toString();
        final RestaurantDto foundRestaurantDto = RestaurantObjectMother.buildRestaurantDto(restaurantSampleData);
        when(this.dataSource.findByName(name)).thenReturn(Optional.of(foundRestaurantDto));

        // When
        Optional<RestaurantEntity> foundRestaurant = this.gateway.findByName(name);

        // Then
        assertThat(foundRestaurant).isPresent();
        final RestaurantEntity expectedFoundRestaurant = RestaurantObjectMother.buildRestaurantEntity(restaurantSampleData);
        assertThat(foundRestaurant.get()).usingRecursiveComparison().isEqualTo(expectedFoundRestaurant);
    }

    @Test
    void shouldDeleteRestaurantById() {
        // When
        final Map<String, Object> restaurantSampleData = getSampleRestaurantData();
        this.gateway.deleteById(UUID.fromString(restaurantSampleData.get("id").toString()));

        // Then
        verify(this.dataSource, times(1)).deleteById(UUID.fromString(restaurantSampleData.get("id").toString()));
    }
}
