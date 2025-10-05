package com.postech.core.item.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder(toBuilder = true)
public class ItemEntity {
    private UUID id;
    private UUID restaurantId;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean availableOnlyAtRestaurant;
    private String photoPath;
    private LocalDateTime lastUpdated;
}
