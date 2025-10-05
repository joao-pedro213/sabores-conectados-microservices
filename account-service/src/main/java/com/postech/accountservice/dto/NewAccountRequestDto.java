package com.postech.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewAccountRequestDto {
    private NewIdentityRequestDto identity;
    @NotBlank
    private String name;
    @NotBlank
    private String email;
    @NotBlank
    private String address;
}
