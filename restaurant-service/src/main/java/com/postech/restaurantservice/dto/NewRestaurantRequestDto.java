package com.postech.restaurantservice.dto;

import com.postech.core.restaurant.domain.entity.enumerator.CuisineType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class NewRestaurantRequestDto {
    @Builder.Default
    private List<UUID> managerIds = new ArrayList<>();
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotNull
    private CuisineType cuisineType;
    @NotNull
    private Map<DayOfWeek, DailyScheduleRequestDto> businessHours;
}
