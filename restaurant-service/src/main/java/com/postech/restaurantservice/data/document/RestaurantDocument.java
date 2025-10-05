package com.postech.restaurantservice.data.document;

import com.postech.core.restaurant.domain.entity.enumerator.CuisineType;
import com.postech.core.restaurant.valueobject.DailySchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Document("restaurants")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDocument {
    @Id
    private UUID id;
    private UUID ownerId;
    private List<UUID> managerIds;
    private String name;
    private String address;
    private CuisineType cuisineType;
    private Map<DayOfWeek, DailySchedule> businessHours;
    @LastModifiedDate
    private LocalDateTime lastUpdated;
}
