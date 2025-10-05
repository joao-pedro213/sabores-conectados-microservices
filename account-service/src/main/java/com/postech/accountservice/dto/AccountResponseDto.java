package com.postech.accountservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class AccountResponseDto {
    private UUID id;
    private UUID identityId;
    private String name;
    private String email;
    private String address;
    private LocalDateTime lastUpdated;
}
