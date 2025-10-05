package com.postech.core.restaurant.presenter;

import com.postech.core.helpers.RestaurantObjectMother;
import com.postech.core.restaurant.domain.entity.RestaurantEntity;
import com.postech.core.restaurant.dto.RestaurantDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RestaurantPresenterTest {
    private RestaurantPresenter presenter;

    @BeforeEach
    void setUp() {
        this.presenter = RestaurantPresenter.build();
    }

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
    void shouldMapDomainToDto() {
        // Given
        final Map<String, Object> restaurantSampleData = getSampleRestaurantData();
        final RestaurantEntity restaurantEntity = RestaurantObjectMother.buildRestaurantEntity(restaurantSampleData);

        // When
        final RestaurantDto restaurantDto = this.presenter.toDto(restaurantEntity);

        // Then
        final RestaurantDto expectedRestaurantDto = RestaurantObjectMother.buildRestaurantDto(restaurantSampleData);
        assertThat(restaurantDto).usingRecursiveComparison().isEqualTo(expectedRestaurantDto);
    }
}
