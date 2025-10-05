package com.postech.aggregatorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

import java.math.BigDecimal;
import java.util.UUID;

@SchemaMapping("OrderItem")
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {
    private UUID id;
    private BigDecimal price;
    private int quantity;
}
