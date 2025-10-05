package com.postech.core.restaurant.domain.entity;

import com.postech.core.restaurant.domain.entity.enumerator.CuisineType;
import com.postech.core.restaurant.valueobject.DailySchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
public class RestaurantEntity {
    private UUID id;
    private UUID ownerId;
    @Builder.Default
    private List<UUID> managerIds = new ArrayList<>();
    private String name;
    private String address;
    private CuisineType cuisineType;
    private Map<DayOfWeek, DailySchedule> businessHours;
    private LocalDateTime lastUpdated;
}
