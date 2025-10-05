package com.postech.aggregatorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantDto {
    private UUID id;
    private UUID ownerId;
    private List<UUID> managerIds;
}
