package com.postech.core.order.domain.usecase;

import com.postech.core.order.domain.entity.OrderEntity;
import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import com.postech.core.order.gateway.OrderGateway;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class CreateOrderUseCase {
    private final OrderGateway gateway;

    public static CreateOrderUseCase build(OrderGateway gateway) {
        return new CreateOrderUseCase(gateway);
    }

    public Mono<OrderEntity> execute(OrderEntity orderEntity) {
        orderEntity.setStatus(OrderStatus.PENDING);
        return this.gateway
                .save(orderEntity)
                .flatMap(savedOrder -> this.gateway.sendEvent(savedOrder).thenReturn(savedOrder));
    }
}
