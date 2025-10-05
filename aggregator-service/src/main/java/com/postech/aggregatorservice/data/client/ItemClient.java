package com.postech.aggregatorservice.data.client;

import com.postech.aggregatorservice.dto.ItemDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Component
public class ItemClient {
    private final WebClient webClient;
    @Value("${gateway.item.route}")
    private String itemRoute;

    public ItemClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<ItemDto> findAllByRestaurantId(UUID restaurantId) {
        return this.webClient
                .get()
                .uri(this.itemRoute + "/restaurant/{restaurantId}/menu", restaurantId)
                .retrieve()
                .bodyToFlux(ItemDto.class);
    }
}
