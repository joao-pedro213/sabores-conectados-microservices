package com.postech.identityservice.dto;

import com.postech.identityservice.api.ApiPatterns;
import com.postech.identityservice.data.document.enumerator.SystemRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewIdentityRequestDto {
    @NotNull
    private SystemRole role;
    @NotBlank
    @Pattern(regexp = ApiPatterns.USERNAME_PATTERN)
    private String username;
    @NotBlank
    @Pattern(regexp = ApiPatterns.PASSWORD_PATTERN)
    private String password;
}
