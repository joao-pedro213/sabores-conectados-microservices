package com.postech.core.account.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class NewAccountDto {
    private UUID identityId;
    private String name;
    private String email;
    private String login;
    private String password;
    private String address;
}
