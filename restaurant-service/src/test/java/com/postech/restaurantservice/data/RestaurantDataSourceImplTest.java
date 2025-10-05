package com.postech.restaurantservice.data;

import com.postech.core.restaurant.dto.RestaurantDto;
import com.postech.restaurantservice.data.mapper.IRestaurantMapper;
import com.postech.restaurantservice.data.document.RestaurantDocument;
import com.postech.restaurantservice.data.repository.IRestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantDataSourceImplTest {
    @Mock
    private IRestaurantRepository mockRepository;

    @Mock
    private IRestaurantMapper mockMapper;

    @InjectMocks
    private RestaurantDataSourceImpl dataSource;

    private static final UUID ID = UUID.randomUUID();

    @Test
    void shouldSaveRestaurant() {
        // Given
        final RestaurantDto restaurantToSaveDto = RestaurantDto.builder().build();
        final RestaurantDocument restaurantToSave = RestaurantDocument.builder().build();
        final RestaurantDto expectedSavedRestaurantDto = RestaurantDto.builder().build();
        when(this.mockMapper.toRestaurantDocument(restaurantToSaveDto)).thenReturn(restaurantToSave);
        when(this.mockRepository.save(restaurantToSave)).thenReturn(restaurantToSave);
        when(this.mockMapper.toRestaurantDto(restaurantToSave)).thenReturn(expectedSavedRestaurantDto);

        // When
        final RestaurantDto savedRestaurantDto = this.dataSource.save(restaurantToSaveDto);

        // Then
        assertThat(savedRestaurantDto).isNotNull().isEqualTo(expectedSavedRestaurantDto);
    }

    @Test
    void shouldFindRestaurantById() {
        // Given
        final UUID restaurantId = UUID.randomUUID();
        final RestaurantDocument foundRestaurant = RestaurantDocument.builder().build();
        when(this.mockRepository.findById(restaurantId)).thenReturn(Optional.of(foundRestaurant));
        final RestaurantDto mappedRestaurantDto = RestaurantDto.builder().build();
        when(this.mockMapper.toRestaurantDto(foundRestaurant)).thenReturn(mappedRestaurantDto);

        // When
        Optional<RestaurantDto> foundRestaurantDto = this.dataSource.findById(restaurantId);

        // Then
        assertThat(foundRestaurantDto).isPresent().contains(mappedRestaurantDto);
    }

    @Test
    void shouldFindRestaurantByName() {
        // Given
        final String restaurantName = "test-restaurant";
        final RestaurantDocument foundRestaurant = RestaurantDocument.builder().build();
        when(this.mockRepository.findByName(restaurantName)).thenReturn(Optional.of(foundRestaurant));
        final RestaurantDto mappedRestaurantDto = RestaurantDto.builder().build();
        when(this.mockMapper.toRestaurantDto(foundRestaurant)).thenReturn(mappedRestaurantDto);

        // When
        Optional<RestaurantDto> foundRestaurantDto = this.dataSource.findByName(restaurantName);

        // Then
        assertThat(foundRestaurantDto).isPresent().contains(mappedRestaurantDto);
    }

    @Test
    void shouldDeleteRestaurantById() {
        // Given
        final UUID restaurantId = UUID.randomUUID();

        // When
        this.dataSource.deleteById(restaurantId);

        // Then
        verify(this.mockRepository, times(1)).deleteById(restaurantId);
    }
}
