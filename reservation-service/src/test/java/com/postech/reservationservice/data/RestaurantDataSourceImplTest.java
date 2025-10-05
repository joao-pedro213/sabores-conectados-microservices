package com.postech.reservationservice.data;

import com.postech.core.restaurant.dto.RestaurantDto;
import com.postech.reservationservice.data.client.RestaurantClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantDataSourceImplTest {

    @Mock
    private RestaurantClient mockClient;

    @InjectMocks
    private RestaurantDataSourceImpl dataSource;

    @Test
    void shouldFindRestaurantById() {
        // Given
        final UUID id = UUID.randomUUID();
        final RestaurantDto expectedRestaurantDto = RestaurantDto.builder().id(id).build();
        when(this.mockClient.findById(id)).thenReturn(Mono.just(expectedRestaurantDto));

        // When
        Mono<RestaurantDto> result = this.dataSource.findById(id);

        // Then
        StepVerifier.create(result).expectNext(expectedRestaurantDto).verifyComplete();
    }
}
