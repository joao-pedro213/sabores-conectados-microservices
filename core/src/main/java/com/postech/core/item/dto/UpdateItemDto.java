package com.postech.core.item.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
public class UpdateItemDto {
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean availableOnlyAtRestaurant;
    private String photoPath;
}
