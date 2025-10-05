package com.postech.aggregatorservice.controller;

import com.postech.aggregatorservice.data.client.ItemClient;
import com.postech.aggregatorservice.dto.ItemDto;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class ItemGraphqlController {
    private final ItemClient client;

    @QueryMapping
    public Flux<ItemDto> restaurantMenu(@Argument("restaurantId") UUID restaurantId) {
        return this.client.findAllByRestaurantId(restaurantId);
    }
}
