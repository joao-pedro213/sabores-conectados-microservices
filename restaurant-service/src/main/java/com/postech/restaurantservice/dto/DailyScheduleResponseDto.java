package com.postech.restaurantservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder(toBuilder = true)
public class DailyScheduleResponseDto {
    private LocalTime openingTime;
    private LocalTime closingTime;
}
