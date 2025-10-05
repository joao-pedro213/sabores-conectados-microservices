package com.postech.aggregatorservice.data.client;

import com.postech.aggregatorservice.dto.OrderDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Component
public class OrderClient {
    private final WebClient webClient;
    @Value("${gateway.order.route}")
    private String orderRoute;

    public OrderClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<OrderDto> findAllByRestaurantId(UUID restaurantId, String status) {
        return this.webClient
                .get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path(this.orderRoute + "/restaurant/{restaurantId}/list");
                    if (status != null && !status.isBlank()) {
                        builder = builder.queryParam("status", status);
                    }
                    return builder.build(restaurantId);
                })
                .retrieve()
                .bodyToFlux(OrderDto.class);
    }
}
