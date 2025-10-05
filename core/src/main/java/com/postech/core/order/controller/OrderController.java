package com.postech.core.order.controller;

import com.postech.core.order.datasource.IOrderDataSource;
import com.postech.core.order.datasource.IOrderMessageProducer;
import com.postech.core.order.domain.entity.OrderEntity;
import com.postech.core.order.domain.entity.OrderItemEntity;
import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import com.postech.core.order.domain.usecase.ChangeOrderStatusUseCase;
import com.postech.core.order.domain.usecase.CreateOrderUseCase;
import com.postech.core.order.domain.usecase.RetrieveOrdersByRestaurantIdUseCase;
import com.postech.core.order.dto.NewOrderDto;
import com.postech.core.order.dto.NewOrderItemDto;
import com.postech.core.order.dto.OrderDto;
import com.postech.core.order.gateway.OrderGateway;
import com.postech.core.order.presenter.OrderPresenter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class OrderController {
    private final OrderGateway gateway;

    public OrderController(IOrderDataSource dataSource, IOrderMessageProducer messageProducer) {
        this.gateway = OrderGateway.build(dataSource, messageProducer);
    }

    public static OrderController build(IOrderDataSource dataSource, IOrderMessageProducer messageProducer) {
        return new OrderController(dataSource, messageProducer);
    }

    public Mono<OrderDto> createOrder(NewOrderDto newOrderDto) {
        return CreateOrderUseCase
                .build(this.gateway)
                .execute(toDomain(newOrderDto))
                .map(OrderPresenter.build()::toDto);
    }

    public Flux<OrderDto> retrieveOrdersByRestaurantId(UUID restaurantId, OrderStatus status) {
        return RetrieveOrdersByRestaurantIdUseCase
                .build(this.gateway)
                .execute(restaurantId, status)
                .map(OrderPresenter.build()::toDto);
    }

    public Mono<OrderDto> changeOrderStatus(UUID id, OrderStatus newStatus) {
        return ChangeOrderStatusUseCase
                .build(this.gateway)
                .execute(id, newStatus)
                .map(OrderPresenter.build()::toDto);
    }

    private static OrderEntity toDomain(NewOrderDto newOrderDto) {
        return toOrderEntity(newOrderDto);
    }

    private static OrderEntity toOrderEntity(NewOrderDto newOrderDto) {
        return OrderEntity
                .builder()
                .restaurantId(newOrderDto.getRestaurantId())
                .customerId(newOrderDto.getCustomerId())
                .items(newOrderDto.getItems().stream().map(OrderController::toOrderItemEntity).toList())
                .build();
    }

    private static OrderItemEntity toOrderItemEntity(NewOrderItemDto newOrderItemDto) {
        return OrderItemEntity
                .builder()
                .id(newOrderItemDto.getId())
                .price(newOrderItemDto.getPrice())
                .quantity(newOrderItemDto.getQuantity())
                .build();
    }
}
