package com.postech.orderservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class OrderItemResponseDto {
    private UUID id;
    private BigDecimal price;
    private int quantity;
}
