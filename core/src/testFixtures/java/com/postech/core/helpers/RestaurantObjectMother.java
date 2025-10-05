package com.postech.core.helpers;

import com.postech.core.restaurant.domain.entity.RestaurantEntity;
import com.postech.core.restaurant.domain.entity.enumerator.CuisineType;
import com.postech.core.restaurant.dto.NewRestaurantDto;
import com.postech.core.restaurant.dto.RestaurantDto;
import com.postech.core.restaurant.dto.UpdateRestaurantDto;
import com.postech.core.restaurant.valueobject.DailySchedule;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RestaurantObjectMother {
    public static RestaurantEntity buildRestaurantEntity(Map<String, Object> sampleData) {
        Map<Object, Map<Object, Object>> businessHoursMap = (Map<Object, Map<Object, Object>>) sampleData.get("businessHours");
        Map<DayOfWeek, DailySchedule> businessHours = new LinkedHashMap<>();
        businessHoursMap
                .forEach((k, v) ->
                        businessHours.put(
                                DayOfWeek.valueOf(k.toString()),
                                DailySchedule
                                        .builder()
                                        .openingTime(LocalTime.parse(v.get("openingTime").toString()))
                                        .closingTime(LocalTime.parse(v.get("closingTime").toString())).build()));
        return RestaurantEntity
                .builder()
                .id(sampleData.get("id") == null ? UUID.randomUUID() : UUID.fromString(sampleData.get("id").toString()))
                .ownerId(sampleData.get("ownerId") == null ? UUID.randomUUID() : UUID.fromString(sampleData.get("ownerId").toString()))
                .managerIds(sampleData.get("managerIds") == null ? Collections.emptyList() : (List<UUID>) sampleData.get("managerIds"))
                .name(sampleData.get("name").toString())
                .address(sampleData.get("address").toString())
                .cuisineType(CuisineType.valueOf(sampleData.get("cuisineType").toString()))
                .businessHours(businessHours)
                .lastUpdated(sampleData.get("lastUpdated") == null ? LocalDateTime.now() : LocalDateTime.parse(sampleData.get("lastUpdated").toString()))
                .build();
    }

    public static NewRestaurantDto buildNewRestaurantDto(Map<String, Object> sampleData) {
        Map<Object, Map<Object, Object>> businessHoursMap = (Map<Object, Map<Object, Object>>) sampleData.get("businessHours");
        Map<DayOfWeek, DailySchedule> businessHours = new LinkedHashMap<>();
        businessHoursMap.forEach((k, v) ->
                businessHours.put(
                        DayOfWeek.valueOf(k.toString()),
                        DailySchedule
                                .builder()
                                .openingTime(LocalTime.parse(v.get("openingTime").toString()))
                                .closingTime(LocalTime.parse(v.get("closingTime").toString())).build()));
        return NewRestaurantDto
                .builder()
                .ownerId(sampleData.get("ownerId") == null ? UUID.randomUUID() : UUID.fromString(sampleData.get("ownerId").toString()))
                .managerIds(sampleData.get("managerIds") == null ? Collections.emptyList() : (List<UUID>) sampleData.get("managerIds"))
                .name(sampleData.get("name").toString())
                .address(sampleData.get("address").toString())
                .cuisineType(CuisineType.valueOf(sampleData.get("cuisineType").toString()))
                .businessHours(businessHours)
                .build();
    }

    public static UpdateRestaurantDto buildUpdateRestaurantDto(Map<String, Object> sampleData) {
        Map<Object, Map<Object, Object>> businessHoursMap = (Map<Object, Map<Object, Object>>) sampleData.get("businessHours");
        Map<DayOfWeek, DailySchedule> businessHours = new LinkedHashMap<>();
        businessHoursMap.forEach((k, v) ->
                businessHours.put(
                        DayOfWeek.valueOf(k.toString()),
                        DailySchedule
                                .builder()
                                .openingTime(LocalTime.parse(v.get("openingTime").toString()))
                                .closingTime(LocalTime.parse(v.get("closingTime").toString())).build()));
        return UpdateRestaurantDto
                .builder()
                .managerIds(sampleData.get("managerIds") == null ? Collections.emptyList() : (List<UUID>) sampleData.get("managerIds"))
                .address(sampleData.get("address").toString())
                .businessHours(businessHours)
                .build();
    }

    public static RestaurantDto buildRestaurantDto(Map<String, Object> sampleData) {
        Map<Object, Map<Object, Object>> businessHoursMap = (Map<Object, Map<Object, Object>>) sampleData.get("businessHours");
        Map<DayOfWeek, DailySchedule> businessHours = new LinkedHashMap<>();
        businessHoursMap
                .forEach((k, v) ->
                        businessHours.put(
                                DayOfWeek.valueOf(k.toString()),
                                DailySchedule
                                        .builder()
                                        .openingTime(LocalTime.parse(v.get("openingTime").toString()))
                                        .closingTime(LocalTime.parse(v.get("closingTime").toString())).build()));
        return RestaurantDto
                .builder()
                .id(sampleData.get("id") == null ? UUID.randomUUID() : UUID.fromString(sampleData.get("id").toString()))
                .ownerId(sampleData.get("ownerId") == null ? UUID.randomUUID() : UUID.fromString(sampleData.get("ownerId").toString()))
                .managerIds(sampleData.get("managerIds") == null ? Collections.emptyList() : (List<UUID>) sampleData.get("managerIds"))
                .name(sampleData.get("name").toString())
                .address(sampleData.get("address").toString())
                .cuisineType(CuisineType.valueOf(sampleData.get("cuisineType").toString()))
                .businessHours(businessHours)
                .lastUpdated(sampleData.get("lastUpdated") == null ? LocalDateTime.now() : LocalDateTime.parse(sampleData.get("lastUpdated").toString()))
                .build();
    }
}
