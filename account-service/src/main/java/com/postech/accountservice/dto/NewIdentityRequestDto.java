package com.postech.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewIdentityRequestDto {
    @NotBlank
    private String role;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
