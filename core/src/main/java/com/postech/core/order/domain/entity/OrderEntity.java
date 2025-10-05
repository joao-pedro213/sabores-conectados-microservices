package com.postech.core.order.domain.entity;

import com.postech.core.order.domain.entity.enumerator.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    private UUID id;
    private UUID restaurantId;
    private UUID customerId;
    private OrderStatus status;
    @Builder.Default
    private List<OrderItemEntity> items = new ArrayList<>();
    private LocalDateTime createdAt;
}
