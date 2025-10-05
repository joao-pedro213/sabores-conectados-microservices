package com.postech.reservationservice.data;

import com.postech.core.reservation.datasource.IRestaurantDataSource;
import com.postech.core.restaurant.dto.RestaurantDto;
import com.postech.reservationservice.data.client.RestaurantClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@AllArgsConstructor
public class RestaurantDataSourceImpl implements IRestaurantDataSource {
    private final RestaurantClient client;

    @Override
    public Mono<RestaurantDto> findById(UUID id) {
        return this.client.findById(id);
    }
}
