package com.postech.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class NewOrderRequestDto {
    @NotNull
    private UUID restaurantId;
    @Builder.Default
    private List<NewOrderItemRequestDto> items = new ArrayList<>();
}
