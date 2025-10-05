package com.postech.itemservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class NewItemRequestDto {
    @NotNull
    private UUID restaurantId;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private BigDecimal price;
    @NotNull
    private Boolean availableOnlyAtRestaurant;
    @NotBlank
    private String photoPath;
}
