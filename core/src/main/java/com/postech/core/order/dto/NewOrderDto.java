package com.postech.core.order.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class NewOrderDto {
    private UUID restaurantId;
    private UUID customerId;
    @Builder.Default
    private List<NewOrderItemDto> items = new ArrayList<>();
}
