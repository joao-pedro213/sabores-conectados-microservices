package com.postech.aggregatorservice.data.client;

import com.postech.aggregatorservice.dto.ReservationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Component
public class ReservationClient {
    private final WebClient webClient;
    @Value("${gateway.reservation.route}")
    private String reservationRoute;

    public ReservationClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<ReservationDto> findAllByRestaurantId(UUID restaurantId, String status) {
        return this.webClient
                .get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path(this.reservationRoute + "/restaurant/{restaurantId}/list");
                    if (status != null && !status.isBlank()) {
                        builder = builder.queryParam("status", status);
                    }
                    return builder.build(restaurantId);
                })
                .retrieve()
                .bodyToFlux(ReservationDto.class);
    }
}
