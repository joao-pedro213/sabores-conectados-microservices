package com.postech.aggregatorservice.data.client;

import com.postech.aggregatorservice.dto.RestaurantDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class RestaurantClient {
    private final WebClient webClient;
    @Value("${gateway.host}")
    private String baseUrl;
    @Value("${gateway.restaurant.route}")
    private String restaurantRoute;

    public RestaurantClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<RestaurantDto> findById(UUID id) {
        return this.webClient
                .get()
                .uri(this.baseUrl + this.restaurantRoute + "/{restaurantId}", id)
                .retrieve()
                .onStatus(httpStatus -> httpStatus == HttpStatus.NOT_FOUND, clientResponse -> Mono.empty())
                .bodyToMono(RestaurantDto.class)
                .filter(restaurantDto -> restaurantDto != null && restaurantDto.getId() != null);
    }
}
