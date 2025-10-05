package com.postech.restaurantservice.controller;

import com.postech.core.restaurant.controller.RestaurantController;
import com.postech.restaurantservice.data.RestaurantDataSourceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class RestaurantControllerFactoryTest {
    @Mock
    private RestaurantDataSourceImpl mockRestaurantDataSourceImpl;

    @InjectMocks
    private RestaurantControllerFactory restaurantControllerFactory;

    @Test
    void shouldReturnRestaurantController() {
        // Given
        RestaurantController expectedController = mock(RestaurantController.class);
        try (MockedStatic<RestaurantController> mockedStatic = Mockito.mockStatic(RestaurantController.class)) {
            mockedStatic.when(() -> RestaurantController.build(this.mockRestaurantDataSourceImpl)).thenReturn(expectedController);

            // When
            RestaurantController actualController = this.restaurantControllerFactory.build();

            // Then
            assertThat(actualController).isNotNull().isEqualTo(expectedController);
        }
    }
}