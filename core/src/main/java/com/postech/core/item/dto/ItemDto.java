package com.postech.core.item.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class ItemDto {
    private UUID id;
    private UUID restaurantId;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean availableOnlyAtRestaurant;
    private String photoPath;
    private LocalDateTime lastUpdated;
}
