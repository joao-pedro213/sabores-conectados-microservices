package com.postech.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponseDto {
    private UUID id;
    private UUID restaurantId;
    private UUID customerId;
    private OrderStatus status;
    @Builder.Default
    private List<OrderItemResponseDto> items = new ArrayList<>();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
