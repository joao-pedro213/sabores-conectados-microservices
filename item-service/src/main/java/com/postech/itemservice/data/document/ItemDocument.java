package com.postech.itemservice.data.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Document("items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDocument {
    @Id
    private UUID id;
    private UUID restaurantId;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean availableOnlyAtRestaurant;
    private String photoPath;
    @LastModifiedDate
    private LocalDateTime lastUpdated;
}
