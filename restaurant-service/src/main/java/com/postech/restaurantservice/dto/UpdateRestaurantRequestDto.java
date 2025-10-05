package com.postech.restaurantservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRestaurantRequestDto {
    @NotNull
    private List<UUID> managerIds;
    @NotBlank
    private String address;
    @NotNull
    private Map<DayOfWeek, DailyScheduleRequestDto> businessHours;
}
