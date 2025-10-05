package com.postech.core.order.presenter;

import com.postech.core.order.domain.entity.OrderEntity;
import com.postech.core.order.domain.entity.OrderItemEntity;
import com.postech.core.order.dto.OrderDto;
import com.postech.core.order.dto.OrderItemDto;

public class OrderPresenter {
    public static OrderPresenter build() {
        return new OrderPresenter();
    }

    public OrderDto toDto(OrderEntity orderEntity) {
        return toOrderDto(orderEntity);
    }

    private static OrderDto toOrderDto(OrderEntity orderEntity) {
        return OrderDto
                .builder()
                .id(orderEntity.getId())
                .restaurantId(orderEntity.getRestaurantId())
                .customerId(orderEntity.getCustomerId())
                .status(orderEntity.getStatus())
                .items(orderEntity.getItems().stream().map(OrderPresenter::toOrderItemDto).toList())
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
}
