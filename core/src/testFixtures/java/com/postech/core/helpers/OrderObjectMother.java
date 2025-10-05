package com.postech.core.helpers;

import com.postech.core.order.domain.entity.OrderEntity;
import com.postech.core.order.domain.entity.OrderItemEntity;
import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import com.postech.core.order.dto.OrderDto;
import com.postech.core.order.dto.OrderItemDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderObjectMother {
    public static OrderEntity buildOrderEntity(Map<String, Object> orderSampleData) {
        return OrderEntity
                .builder()
                .id(UUID.fromString(orderSampleData.get("id").toString()))
                .restaurantId((UUID) orderSampleData.get("restaurantId"))
                .customerId(UUID.fromString(orderSampleData.get("customerId").toString()))
                .status(OrderStatus.valueOf(orderSampleData.get("status").toString()))
                .items(buildOrderItemEntityList((List<Map<String, Object>>) orderSampleData.get("items")))
                .createdAt((LocalDateTime) orderSampleData.get("createdAt"))
                .build();
    }

    public static OrderDto buildOrderDto(Map<String, Object> orderSampleData) {
        return OrderDto.builder()
                .id(UUID.fromString(orderSampleData.get("id").toString()))
                .restaurantId((UUID) orderSampleData.get("restaurantId"))
                .customerId(UUID.fromString(orderSampleData.get("customerId").toString()))
                .status(OrderStatus.valueOf(orderSampleData.get("status").toString()))
                .items(buildOrderItemDtoList((List<Map<String, Object>>) orderSampleData.get("items")))
                .createdAt((LocalDateTime) orderSampleData.get("createdAt"))
                .build();
    }

    public static List<OrderItemEntity> buildOrderItemEntityList(List<Map<String, Object>> orderItemSampleData) {
        return orderItemSampleData
                .stream()
                .map(sampleItem ->
                        OrderItemEntity
                                .builder()
                                .id(UUID.fromString(sampleItem.get("id").toString()))
                                .price(BigDecimal.valueOf(Double.parseDouble(sampleItem.get("price").toString())))
                                .quantity((int) sampleItem.get("quantity"))
                                .build())
                .toList();
    }

    public static List<OrderItemDto> buildOrderItemDtoList(List<Map<String, Object>> orderItemSampleData) {
        return orderItemSampleData
                .stream()
                .map(sampleItem ->
                        OrderItemDto
                                .builder()
                                .id(UUID.fromString(sampleItem.get("id").toString()))
                                .price(BigDecimal.valueOf(Double.parseDouble(sampleItem.get("price").toString())))
                                .quantity((int) sampleItem.get("quantity"))
                                .build())
                .toList();
    }
}
