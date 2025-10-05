package com.postech.core.order.datasource;

import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import com.postech.core.order.dto.OrderDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IOrderDataSource {
    Mono<OrderDto> save(OrderDto orderDto);

    Mono<OrderDto> findById(UUID id);

    Flux<OrderDto> findAllByRestaurantId(UUID restaurantId);

    Flux<OrderDto> findAllByRestaurantIdAndStatus(UUID restaurantId, OrderStatus status);
}
