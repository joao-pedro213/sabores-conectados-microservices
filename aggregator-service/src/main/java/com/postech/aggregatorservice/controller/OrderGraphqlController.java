package com.postech.aggregatorservice.controller;

import com.postech.aggregatorservice.data.client.OrderClient;
import com.postech.aggregatorservice.dto.OrderDto;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class OrderGraphqlController {
    private final OrderClient client;

    @QueryMapping
    @PreAuthorize("@securityService.canReadRestaurantOrders(#restaurantId)")
    public Flux<OrderDto> restaurantOrders(
            @Argument("restaurantId") UUID restaurantId,
            @Argument("status") String status) {
        return this.client.findAllByRestaurantId(restaurantId, status);
    }
}
