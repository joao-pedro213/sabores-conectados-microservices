package com.postech.aggregatorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@SchemaMapping("Item")
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
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

