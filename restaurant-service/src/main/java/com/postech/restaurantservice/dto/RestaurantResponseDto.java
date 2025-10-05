package com.postech.restaurantservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponseDto {
    private UUID id;
    private UUID ownerId;
    @Builder.Default
    private List<UUID> managerIds = new ArrayList<>();
    private String name;
    private String address;
    private String cuisineType;
    private Map<DayOfWeek, DailyScheduleResponseDto> businessHours;
    private LocalDateTime lastUpdated;
}
