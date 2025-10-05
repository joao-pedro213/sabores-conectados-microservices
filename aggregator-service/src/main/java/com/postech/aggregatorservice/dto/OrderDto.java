package com.postech.aggregatorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SchemaMapping("Order")
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private UUID id;
    private UUID restaurantId;
    private UUID customerId;
    private String status;
    @Builder.Default
    private List<OrderItemDto> items = new ArrayList<>();
    private LocalDateTime createdAt;
}
