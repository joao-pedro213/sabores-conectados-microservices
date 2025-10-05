package com.postech.core.order.gateway;

import com.postech.core.order.datasource.IOrderDataSource;
import com.postech.core.order.datasource.IOrderMessageProducer;
import com.postech.core.order.domain.entity.OrderEntity;
import com.postech.core.order.domain.entity.OrderItemEntity;
import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import com.postech.core.order.dto.OrderDto;
import com.postech.core.order.dto.OrderItemDto;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@AllArgsConstructor
public class OrderGateway {
    private final IOrderDataSource dataSource;
    private final IOrderMessageProducer messageProducer;

    public static OrderGateway build(IOrderDataSource dataSource, IOrderMessageProducer eventProducer) {
        return new OrderGateway(dataSource, eventProducer);
    }

    public Mono<OrderEntity> save(OrderEntity orderEntity) {
        return this.dataSource.save(toDto(orderEntity)).map(OrderGateway::toDomain);
    }

    public Mono<OrderEntity> findById(UUID id) {
        return this.dataSource.findById(id).map(OrderGateway::toDomain);
    }

    public Flux<OrderEntity> findAllByRestaurantId(UUID restaurantId) {
        return this.dataSource.findAllByRestaurantId(restaurantId).map(OrderGateway::toDomain);
    }

    public Flux<OrderEntity> findAllByRestaurantIdAndStatus(UUID restaurantId, OrderStatus status) {
        return this.dataSource.findAllByRestaurantIdAndStatus(restaurantId, status).map(OrderGateway::toDomain);
    }

    public Mono<Void> sendEvent(OrderEntity orderEntity) {
        return this.messageProducer.sendMessage(toOrderDto(orderEntity));
    }

    private static OrderDto toDto(OrderEntity orderEntity) {
        return toOrderDto(orderEntity);
    }

    private static OrderDto toOrderDto(OrderEntity orderEntity) {
        return OrderDto
                .builder()
                .id(orderEntity.getId())
                .restaurantId(orderEntity.getRestaurantId())
                .customerId(orderEntity.getCustomerId())
                .status(orderEntity.getStatus())
                .items(orderEntity.getItems().stream().map(OrderGateway::toOrderItemDto).toList())
                .createdAt(orderEntity.getCreatedAt())
                .build();
    }

    private static OrderItemDto toOrderItemDto(OrderItemEntity orderItemEntity) {
        return OrderItemDto
                .builder()
                .id(orderItemEntity.getId())
                .price(orderItemEntity.getPrice())
                .quantity(orderItemEntity.getQuantity())
                .build();
    }

    private static OrderEntity toDomain(OrderDto orderDto) {
        return toOrderEntity(orderDto);
    }

    private static OrderEntity toOrderEntity(OrderDto orderDto) {
        return OrderEntity
                .builder()
                .id(orderDto.getId())
                .restaurantId(orderDto.getRestaurantId())
                .customerId(orderDto.getCustomerId())
                .status(orderDto.getStatus())
                .items(orderDto.getItems().stream().map(OrderGateway::toOrderItemEntity).toList())
                .createdAt(orderDto.getCreatedAt())
                .build();
    }

    private static OrderItemEntity toOrderItemEntity(OrderItemDto orderItemDto) {
        return OrderItemEntity
                .builder()
                .id(orderItemDto.getId())
                .price(orderItemDto.getPrice())
                .quantity(orderItemDto.getQuantity())
                .build();
    }
}
