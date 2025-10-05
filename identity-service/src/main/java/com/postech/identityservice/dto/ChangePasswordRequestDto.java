package com.postech.identityservice.dto;

import com.postech.identityservice.api.ApiPatterns;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangePasswordRequestDto {
    @NotBlank
    private String username;
    @NotBlank
    private String oldPassword;
    @NotBlank
    @Pattern(regexp = ApiPatterns.PASSWORD_PATTERN)
    private String newPassword;
}
