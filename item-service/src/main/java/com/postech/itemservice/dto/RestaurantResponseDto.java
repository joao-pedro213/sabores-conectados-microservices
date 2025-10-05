package com.postech.itemservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class RestaurantResponseDto {
    private UUID id;
    private UUID ownerId;
    private String name;
    private String address;
    private String cuisineType;
    private Map<DayOfWeek, DailyScheduleResponseDto> businessHours;
    private LocalDateTime lastUpdated;
}
