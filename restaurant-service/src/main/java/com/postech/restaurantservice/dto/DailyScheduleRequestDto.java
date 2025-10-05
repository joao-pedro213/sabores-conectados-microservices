package com.postech.restaurantservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder(toBuilder = true)
public class DailyScheduleRequestDto {
    private LocalTime openingTime;
    private LocalTime closingTime;
}
