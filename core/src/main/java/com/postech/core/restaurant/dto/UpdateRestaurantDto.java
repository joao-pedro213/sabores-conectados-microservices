package com.postech.core.restaurant.dto;

import com.postech.core.restaurant.valueobject.DailySchedule;
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
public class UpdateRestaurantDto {
    @Builder.Default
    private List<UUID> managerIds = new ArrayList<>();
    private String address;
    private Map<DayOfWeek, DailySchedule> businessHours;
}
