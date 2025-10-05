package com.postech.aggregatorservice.controller;

import com.postech.aggregatorservice.data.client.ReservationClient;
import com.postech.aggregatorservice.dto.ReservationDto;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Controller
@AllArgsConstructor
public class ReservationGraphqlController {
    private final ReservationClient client;

    @QueryMapping
    @PreAuthorize("@securityService.canReadRestaurantReservations(#restaurantId)")
    public Flux<ReservationDto> restaurantReservations(
            @Argument("restaurantId") UUID restaurantId,
            @Argument("status") String status) {
        return this.client.findAllByRestaurantId(restaurantId, status);
    }
}
