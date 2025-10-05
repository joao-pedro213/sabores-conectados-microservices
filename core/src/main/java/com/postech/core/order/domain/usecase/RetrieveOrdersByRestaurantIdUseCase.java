package com.postech.core.order.domain.usecase;

import com.postech.core.order.domain.entity.OrderEntity;
import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import com.postech.core.order.gateway.OrderGateway;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.UUID;

@AllArgsConstructor
public class RetrieveOrdersByRestaurantIdUseCase {
    private final OrderGateway gateway;

    public static RetrieveOrdersByRestaurantIdUseCase build(OrderGateway gateway) {
        return new RetrieveOrdersByRestaurantIdUseCase(gateway);
    }

    public Flux<OrderEntity> execute(UUID restaurantId, OrderStatus status) {
        if (status == null) {
            return this.gateway.findAllByRestaurantId(restaurantId);
        } else {
            return this.gateway.findAllByRestaurantIdAndStatus(restaurantId, status);
        }
    }
}
