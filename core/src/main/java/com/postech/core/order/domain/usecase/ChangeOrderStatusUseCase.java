package com.postech.core.order.domain.usecase;

import com.postech.core.common.exception.EntityNotFoundException;
import com.postech.core.order.domain.entity.OrderEntity;
import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import com.postech.core.order.gateway.OrderGateway;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@AllArgsConstructor
public class ChangeOrderStatusUseCase {
    private final OrderGateway gateway;

    public static ChangeOrderStatusUseCase build(OrderGateway gateway) {
        return new ChangeOrderStatusUseCase(gateway);
    }

    public Mono<OrderEntity> execute(UUID id, OrderStatus newStatus) {
        return this.gateway
                .findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Order")))
                .map(orderEntity -> orderEntity.toBuilder().status(newStatus).build())
                .flatMap(this.gateway::save)
                .flatMap(savedOrder -> this.gateway.sendEvent(savedOrder).thenReturn(savedOrder));
    }
}
