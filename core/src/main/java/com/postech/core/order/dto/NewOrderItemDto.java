package com.postech.core.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class NewOrderItemDto {
    private UUID id;
    private BigDecimal price;
    private int quantity;
}
