package com.postech.orderservice.data.client;

import com.postech.core.restaurant.dto.RestaurantDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantClientTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private RestaurantClient restaurantClient;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(this.restaurantClient, "baseUrl", "http://dummy-url");
        ReflectionTestUtils.setField(this.restaurantClient, "restaurantRoute", "/restaurants");
        when(this.webClient.get()).thenReturn(this.requestHeadersUriSpec);
        when(this.requestHeadersUriSpec.uri(anyString(), any(UUID.class))).thenReturn(this.requestHeadersSpec);
        when(this.requestHeadersSpec.retrieve()).thenReturn(this.responseSpec);
    }

    @Test
    void findByIdReturnsRestaurantDtoWhenRestaurantExists() {
        // Given
        UUID restaurantId = UUID.randomUUID();
        RestaurantDto expectedDto = RestaurantDto.builder().id(restaurantId).name("Test Restaurant").build();
        when(this.responseSpec.onStatus(any(), any())).thenReturn(this.responseSpec);
        when(this.responseSpec.bodyToMono(RestaurantDto.class)).thenReturn(Mono.just(expectedDto));

        // When
        Mono<RestaurantDto> result = this.restaurantClient.findById(restaurantId);

        // Then
        StepVerifier.create(result).expectNext(expectedDto).verifyComplete();
    }

    @Test
    void findByIdReturnsEmptyWhenRestaurantNotFound() {
        // Given
        when(this.responseSpec.onStatus(any(), any())).thenReturn(this.responseSpec);
        when(this.responseSpec.bodyToMono(RestaurantDto.class)).thenReturn(Mono.empty());

        // When
        Mono<RestaurantDto> result = this.restaurantClient.findById(UUID.randomUUID());

        // Then
        StepVerifier.create(result).verifyComplete();
    }
}
